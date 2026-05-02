CREATE DATABASE qedu_db;
CREATE TYPE qedu_role AS ENUM ('ADMIN', 'TEACHER', 'CHEF', 'STUDENT');
CREATE TYPE attendance_status AS ENUM ('PRESENT', 'LATE', 'EXCUSED', 'ABSENT');

CREATE TABLE qedu_users (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    roles qedu_role NOT NULL,
    class_groups VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_qedu_users_role ON qedu_users (roles);
CREATE INDEX idx_qedu_users_class_groups ON qedu_users (class_groups);

CREATE TABLE qedutimetable_entries (
    id BIGSERIAL PRIMARY KEY,
    day_of_the_week INTEGER NOT NULL CHECK (day_of_the_week BETWEEN 1 AND 5),
    lesson_idx INTEGER NOT NULL CHECK (lesson_idx BETWEEN 1 AND 10),
    class_groups VARCHAR(255) NOT NULL,
    subject_names VARCHAR(255) NOT NULL,
    teacher_user_name VARCHAR(255) NOT NULL,
    teacher_name VARCHAR(255) NOT NULL,
    class_room_name VARCHAR(255) NOT NULL,
    lesson_starts_at TIME NOT NULL,
    lesson_ends_at TIME NOT NULL,

    CONSTRAINT chk_timetable_time CHECK (lesson_ends_at > lesson_starts_at),
    CONSTRAINT uq_timetable_teacher_slot UNIQUE (day_of_the_week, lesson_idx, teacher_user_name),
    CONSTRAINT uq_timetable_class_slot UNIQUE (day_of_the_week, lesson_idx, class_groups),
    CONSTRAINT uq_timetable_room_slot UNIQUE (day_of_the_week, lesson_idx, class_room_name)
);

CREATE INDEX idx_timetable_teacher ON qedutimetable_entries (teacher_user_name);
CREATE INDEX idx_timetable_class ON qedutimetable_entries (class_groups);
CREATE INDEX idx_timetable_day_lesson ON qedutimetable_entries (day_of_the_week, lesson_idx);


CREATE TABLE qedugrade_records (
    id BIGSERIAL PRIMARY KEY,
    student_username VARCHAR(255),
    student_name VARCHAR(255),
    class_group VARCHAR(255),
    subject_name VARCHAR(255),
    grade INTEGER CHECK (grade BETWEEN 1 AND 5),
    weight_grades DOUBLE PRECISION DEFAULT 1.0,
    notes TEXT,
    taecher_username VARCHAR(255), -- kept as in your Java field typo: taecherUsername
    teacher_name VARCHAR(255),
    created_time TIMESTAMP
);

CREATE INDEX idx_grades_student_username ON qedugrade_records (student_username);
CREATE INDEX idx_grades_teacher_name ON qedugrade_records (teacher_name);
CREATE INDEX idx_grades_class_group ON qedugrade_records (class_group);

CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    attendance_date DATE,
    lesson_index INTEGER,
    class_group VARCHAR(255),
    subject_name VARCHAR(255),
    student_username VARCHAR(255),
    student_name VARCHAR(255),
    teacher_username VARCHAR(255),
    attendance_status attendance_status
);

CREATE INDEX idx_attendance_student_username ON attendance_records (student_username);
CREATE INDEX idx_attendance_teacher_username ON attendance_records (teacher_username);
CREATE INDEX idx_attendance_date ON attendance_records (attendance_date);
CREATE INDEX idx_attendance_status ON attendance_records (attendance_status);

CREATE TABLE qeduschool_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    event_title VARCHAR(10) NOT NULL,
    event_description VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL,
    audience VARCHAR(255) NOT NULL,
    set_related_timetable_id BIGINT NOT NULL,
    event_start_time TIMESTAMP NOT NULL,
    event_end_time TIMESTAMP NOT NULL,
    created_by_user VARCHAR(255) NOT NULL,

    CONSTRAINT chk_event_time CHECK (event_end_time > event_start_time),
    CONSTRAINT fk_event_timetable FOREIGN KEY (set_related_timetable_id)
        REFERENCES qedutimetable_entries(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_events_audience ON qeduschool_events (audience);
CREATE INDEX idx_events_start_time ON qeduschool_events (event_start_time);
CREATE INDEX idx_events_created_by ON qeduschool_events (created_by_user);

CREATE TABLE canteen_menus (
    id BIGSERIAL PRIMARY KEY,
    menu_date DATE NOT NULL UNIQUE,
    main_meal VARCHAR(255) NOT NULL,
    soup VARCHAR(255),
    side_dish VARCHAR(255),
    drink VARCHAR(255),
    dessert VARCHAR(255),
    created_by_chef VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_canteen_menu_date ON canteen_menus (menu_date);
CREATE INDEX idx_canteen_created_by_chef ON canteen_menus (created_by_chef);
