package org.backend.qedu.config;
import org.backend.qedu.entities.*;
import org.backend.qedu.model.*;
import org.backend.qedu.repo.*;
import org.backend.qedu.canteen.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Demo / teszt adatbázis egy induló rendszerhez.
 * <p>
 * Üres {@code users} tábla esén teljes feltöltés fut; ha már vannak felhasználók, csak az üres
 * táblák egészülnek ki (osztály–tántárgy, teszt/projekt/anyag, üzenetek).
 * </p>
 * <p><b>Példa belépések (felhasználónév / jelszó):</b></p>
 * <ul>
 *   <li>Admin: {@code admin} / {@code admin31}</li>
 *   <li>Tanár (I.D Programozás): {@code molnara} / {@code molnara51}</li>
 *   <li>Tanár (II.D): {@code balogb} / {@code bence14}</li>
 *   <li>Diák (I.D): {@code kulcsara} / {@code kulcsara21}</li>
 *   <li>Diák (I.D): {@code kovacsj} / {@code kovacsj31}</li>
 *   <li>Séf / étlap: {@code chefp} / {@code peterchef89}</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class DataSeed {
    private final UserRepo userRepository;
    private final ClassSubjectRepo classSubjectRepository;
    private final TimetableRepo timetableRepository;
    private final EventRepo eventRepository;
    private final GradeRepo gradeRepository;
    private final CanteenRepo canteenRepository;
    private final AttendanceRepo attendanceRepository;
    private final QEduExamRepo qeduExamRepository;
    private final QEduProjectRepo qeduProjectRepository;
    private final StudyMaterialRepo studyMaterialRepository;
    private final MessageThreadRepo messageThreadRepository;
    private final StudentSubjectEnrollmentRepo studentSubjectEnrollmentRepository;
    private final PasswordEncoder passwordEncoder;
    private User admin;

    private static final String GRADING_JSON_SAMPLE =
            "{\"minPointsFor5\":45,\"minPointsFor4\":38,\"minPointsFor3\":30,\"minPointsFor2\":20}";
    @Bean
    CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() > 0) {
                seedClassSubjectsIfEmpty();
                seedEnrollmentsIfEmpty();
                ensureKovacsJDemoStudent();
                seedExamsProjectsMaterialsIfEmpty();
                seedMessageThreadsIfEmpty();
                return;
            }

            admin = createUser("admin", "admin31", "Administrator", Roles.ADMIN, null);
            createUser("molnara", "molnara51", "Molnar Andras", Roles.TEACHER, "I.D");
            createUser("bonat", "tamas12", "Bona Tamas", Roles.TEACHER, "IV.D");
            createUser("vighl", "vigh14", "Vigh Laszlo", Roles.TEACHER, "III.D");
            createUser("balogb", "bence14", "Balog Bence", Roles.TEACHER, "II.D");
            createUser("kulcsara", "kulcsara21", "Kulcsar Adam", Roles.STUDENT, "I.D");
            createUser("kovacsj", "kovacsj31", "Kovacs Janos", Roles.STUDENT, "I.D");
            createUser("violak", "violak43", "Viola Krisztian", Roles.STUDENT, "IV.D");
            createUser("mazant", "mazant52", "Mazan Tamas", Roles.STUDENT, "II.D");
            createUser("bbotond", "baloghb92", "Balogh Botond", Roles.STUDENT, "III.D");
            createUser("bonaa", "bonaa22", "Bona Adam", Roles.STUDENT, "II.D");
            createUser("chefp", "peterchef89", "Nagy Peter", Roles.CHEF, null);

            createTimetable(1, 1, "I.D", "Programozas", "molnara", "Molnar Andras", "G-210", "08:50", "09:35");
            createTimetable(1, 1, "IV.D", "Programozas", "bonat", "Bona Tamas", "G-210", "08:00", "08:45");
            createTimetable(4, 5, "III.D", "Adatstrukturak", "vighl", "Vigh Laszlo", "K-101", "10:30", "11:15");
            createTimetable(5, 6, "II.D", "Programozas", "balogb", "Balog Bence", "DP005", "12:15", "13:00");

            createAttendance("kulcsara", "Kulcsar Adam", "I.D", "Programozas", AttendanceStatus.Status.PRESENT, "molnara", "Molnar Andras", "Molnar Andras");
            createAttendance("violak", "Viola Krisztian", "IV.D", "Programozas", AttendanceStatus.Status.LATE, "bonat", "Bona Tamas", "Bona Tamas");
            createAttendance("mazant", "Mazan Tamas", "II.D", "Adatstrukturak", AttendanceStatus.Status.ABSENT, "balogb", "Balog Bence", "Balog Bence");
            createAttendance("bbotond", "Balogh Botond", "III.D", "Matematika", AttendanceStatus.Status.EXCUSED, "vighl", "Vigh Laszlo", "Vigh Laszlo");
            createAttendance("bonaa", "Bona Adam", "II.D", "Adatstrukturak", AttendanceStatus.Status.PRESENT, "balogb", "Balog Bence", "Balog Bence");

            createGrade("kulcsara", "Kulcsar Adam", "I.D", "Programozas", 1, "Kituno", "molnara","Molnar Andras",LocalDateTime.now());
            createGrade("violak", "Viola Krisztian", "IV.D", "Programozas", 2, "Dicseretes", "bonat","Bona Tamas",LocalDateTime.now());
            createGrade("mazant", "Mazan Tamas", "II.D", "Adatstrukturak", 3, "Kozepes", "balogb","Balog Bence",LocalDateTime.now());
            createGrade("bbotond", "Balogh Botond", "III.D", "Matematika", 3, "Kozepes", "vighl","Vigh Laszlo",LocalDateTime.now());
            createGrade("bonaa", "Bona Adam", "II.D", "Adatstrukturak", 4, "Elegseges", "balogb","Balog Bence",LocalDateTime.now());

            createEvent("Helyettesites", "Helyettesites", "helyettesites tanaroknak", "mindenki",admin.getFullName());
            createEvent("Kirandulas", "Kirandulas", "egynapos kirandulas", "mindenki", admin.getFullName());
            createEvent("Szunidok","Szunido","Szunido az iskolaban","mindenki", admin.getFullName());
            createEvent("Helyettesites","Helyettesites","Helyettesites az orarendben","mindenki",admin.getFullName());

            createCanteenMenu("mindenki","Nagy Peter","Csibe rizsaval","Sertes kompottal","Leves","Masikfele","Paradicsomleves","Coca-Cola","Tiramisu",1,"Nagy Peter");
            createCanteenMenu("mindenki","Nagy Peter","Gordonblue","Zavtos hus","Leves","Masikfele","gombocleves","Sprite","Galuska",2,"Nagy Peter");
            createCanteenMenu("mindenki","Nagy Peter","GrillSajt","GrillHal","Leves","Masikfele","husleves","Light Cola","Sajttorta",1,"Nagy Peter");

            seedClassSubjectsIfEmpty();
            seedEnrollmentsIfEmpty();
            seedExamsProjectsMaterialsIfEmpty();
            seedMessageThreadsIfEmpty();
        };
    }

    private void seedMessageThreadsIfEmpty() {
        if (messageThreadRepository.count() > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();

        MessageThread all = new MessageThread();
        all.setTitle("Iskolai kozlemeny");
        all.setLastMessage("Udv mindenkinek — teszt uzenet az uj feluleten.");
        all.setAuthorUsername("admin");
        all.setAuthorRole(Roles.ADMIN.name());
        all.setAudienceJson("[\"ALL\"]");
        all.setClassTargetsJson("[]");
        all.setCreatedAt(now);
        messageThreadRepository.save(all);

        MessageThread idClass = new MessageThread();
        idClass.setTitle("I.D — Programozas emlekezteto");
        idClass.setLastMessage("Kerlek nezzetek meg a feltoltott tananyagot.");
        idClass.setAuthorUsername("molnara");
        idClass.setAuthorRole(Roles.TEACHER.name());
        idClass.setAudienceJson("[\"STUDENT\"]");
        idClass.setClassTargetsJson("[\"I.D\"]");
        idClass.setCreatedAt(now.minusMinutes(5));
        messageThreadRepository.save(idClass);
    }

    private void seedExamsProjectsMaterialsIfEmpty() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (qeduExamRepository.count() == 0) {
            saveExam(
                    "I. fejezet — temazaro (Programozas)",
                    "I.D",
                    "I.D",
                    "prog",
                    "Programozas",
                    50,
                    today.plusDays(7),
                    "scheduled",
                    "manual",
                    GRADING_JSON_SAMPLE,
                    null,
                    "molnara"
            );
            saveExam(
                    "Adatszerkezetek ropdolgozat",
                    "II.D",
                    "II.D",
                    "adt",
                    "Adatstrukturak",
                    40,
                    today.plusDays(2),
                    "closed",
                    "manual",
                    GRADING_JSON_SAMPLE,
                    null,
                    "balogb"
            );
        }

        if (qeduProjectRepository.count() == 0) {
            saveProject(
                    "Mini projekt — alkalmazas vazlat",
                    "Keszits vazlatot a tanult mintakrol (min. 1 oldal).",
                    "I.D",
                    "I.D",
                    "prog",
                    "Programozas",
                    "active",
                    today,
                    today.plusWeeks(2),
                    35,
                    "molnara",
                    now
            );
            saveProject(
                    "Adatszerkezetek gyakorlo feladat",
                    "Implementalj egyszeru listat es bejarast.",
                    "II.D",
                    "II.D",
                    "adt",
                    "Adatstrukturak",
                    "planned",
                    today.plusDays(1),
                    today.plusWeeks(3),
                    0,
                    "balogb",
                    now
            );
        }

        if (studyMaterialRepository.count() == 0) {
            saveStudyMaterial(
                    "01 Bevezetes — jegyzet",
                    "I.D",
                    "I.D",
                    "prog",
                    "Programozas",
                    "document",
                    "active",
                    today,
                    today.plusMonths(2),
                    "molnara",
                    now
            );
            saveStudyMaterial(
                    "Lancolt listak — prezentacio",
                    "II.D",
                    "II.D",
                    "adt",
                    "Adatstrukturak",
                    "presentation",
                    "active",
                    today,
                    today.plusMonths(1),
                    "balogb",
                    now
            );
        }
    }

    private void saveExam(
            String title,
            String classId,
            String className,
            String subjectId,
            String subjectName,
            int maxPoints,
            LocalDate examDate,
            String status,
            String kind,
            String gradingJson,
            String questionsJson,
            String teacherUsername
    ) {
        QEduExam e = new QEduExam();
        e.setTitle(title);
        e.setClassId(classId);
        e.setClassName(className);
        e.setSubjectId(subjectId);
        e.setSubjectName(subjectName);
        e.setMaxPoints(maxPoints);
        e.setExamDate(examDate);
        e.setStatus(status);
        e.setKind(kind);
        e.setGradingJson(gradingJson);
        e.setQuestionsJson(questionsJson);
        e.setTeacherUsername(teacherUsername);
        qeduExamRepository.save(e);
    }

    private void saveProject(
            String title,
            String description,
            String classId,
            String className,
            String subjectId,
            String subjectName,
            String status,
            LocalDate startsAt,
            LocalDate deadline,
            int progress,
            String teacherUsername,
            LocalDateTime updatedAt
    ) {
        QEduProject p = new QEduProject();
        p.setTitle(title);
        p.setDescription(description);
        p.setClassId(classId);
        p.setClassName(className);
        p.setSubjectId(subjectId);
        p.setSubjectName(subjectName);
        p.setStatus(status);
        p.setStartsAt(startsAt);
        p.setDeadline(deadline);
        p.setProgress(progress);
        p.setTeacherUsername(teacherUsername);
        p.setUpdatedAt(updatedAt);
        qeduProjectRepository.save(p);
    }

    private void saveStudyMaterial(
            String title,
            String classId,
            String className,
            String subjectId,
            String subjectName,
            String materialType,
            String status,
            LocalDate validFrom,
            LocalDate validTo,
            String teacherUsername,
            LocalDateTime updatedAt
    ) {
        StudyMaterial m = new StudyMaterial();
        m.setTitle(title);
        m.setClassId(classId);
        m.setClassName(className);
        m.setSubjectId(subjectId);
        m.setSubjectName(subjectName);
        m.setMaterialType(materialType);
        m.setStatus(status);
        m.setValidFrom(validFrom);
        m.setValidTo(validTo);
        m.setTeacherUsername(teacherUsername);
        m.setUpdatedAt(updatedAt);
        studyMaterialRepository.save(m);
    }

    private void seedClassSubjectsIfEmpty() {
        if (classSubjectRepository.count() > 0) {
            return;
        }
        saveClassSubject("cid-id", "I.D", "prog", "Programozas", "molnara", "Molnar Andras");
        saveClassSubject("cid-ivd", "IV.D", "prog", "Programozas", "bonat", "Bona Tamas");
        saveClassSubject("cid-iiid", "III.D", "matek", "Matematika", "vighl", "Vigh Laszlo");
        saveClassSubject("cid-iid", "II.D", "adt", "Adatstrukturak", "balogb", "Balog Bence");
        
        saveClassSubject("cid-iidprog", "II.D", "prog", "Programozas", "balogb", "Balog Bence");
    }


    private void seedEnrollmentsIfEmpty() {
        if (studentSubjectEnrollmentRepository.count() > 0 || classSubjectRepository.count() == 0) {
            return;
        }
        for (ClassSubjectAssignment a : classSubjectRepository.findAll()) {
            for (User u : userRepository.findByRolesAndClassGroups(Roles.STUDENT, a.getClassName())) {
                StudentSubjectEnrollment e = new StudentSubjectEnrollment();
                e.setStudent(u);
                e.setClassSubject(a);
                studentSubjectEnrollmentRepository.save(e);
            }
        }
    }

    private void ensureKovacsJDemoStudent() {
        User u = userRepository.findByUserName("kovacsj").orElseGet(() ->
                createUser("kovacsj", "kovacsj31", "Kovacs Janos", Roles.STUDENT, "I.D"));
        if (!u.isEnabled()) {
            u.setEnabled(true);
            userRepository.save(u);
        }
        if (u.getRoles() != Roles.STUDENT || u.getClassGroups() == null || u.getClassGroups().isBlank()) {
            return;
        }
        for (ClassSubjectAssignment a : classSubjectRepository.findAllByOrderByClassIdAscSubjectNameAsc()) {
            if (!Objects.equals(a.getClassName(), u.getClassGroups())) {
                continue;
            }
            if (!studentSubjectEnrollmentRepository.existsByClassSubject_IdAndStudent_ID(a.getId(), u.getID())) {
                StudentSubjectEnrollment e = new StudentSubjectEnrollment();
                e.setStudent(u);
                e.setClassSubject(a);
                studentSubjectEnrollmentRepository.save(e);
            }
        }
    }
    
    private void saveClassSubject(
            String classId,
            String className,
            String subjectId,
            String subjectName,
            String teacherUsername,
            String teacherName
    ) {
        ClassSubjectAssignment a = new ClassSubjectAssignment();
        a.setClassId(classId);
        a.setClassName(className);
        a.setSubjectId(subjectId);
        a.setSubjectName(subjectName);
        a.setTeacherUsername(teacherUsername);
        a.setTeacherName(teacherName);
        classSubjectRepository.save(a);
    }

    private User createUser(String username, String password, String fullName, Roles roles, String classGroup) {

        return userRepository.findByUserName(username).orElseGet(() -> {
                    User user = new User();

                    user.setUserName(username);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setFullName(fullName);
                    user.setRoles(roles);
                    user.setClassGroups(classGroup);
                    user.setEnabled(true);
                    user.setVerified(true);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                });
    }

    private void createTimetable(
            Integer day,
            Integer lesson,
            String classGroup,
            String subject,
            String teacherUsername,
            String teacherName,
            String room,
            String start,
            String end
    ) {
        Timetable timetable = new Timetable();

        timetable.setDayOfTheWeek(day);
        timetable.setLessonIdx(lesson);
        timetable.setClassGroups(classGroup);
        timetable.setSubjectNames(subject);
        timetable.setTeacherUserName(teacherUsername);
        timetable.setTeacherName(teacherName);
        timetable.setClassRoomName(room);
        timetable.setLessonStartsAt(LocalTime.parse(start));
        timetable.setLessonEndsAt(LocalTime.parse(end));

        timetableRepository.save(timetable);
    }

    private void createGrade(
            String studentUsername,
            String studentName,
            String classGroup,
            String subject,
            Integer grades,
            String note,
            String teacherUsername,
            String teacherName,
            LocalDateTime createdDateTime
    ) {
        GradeRecords grade = new GradeRecords();

        grade.setStudentUsername(studentUsername);
        grade.setStudentName(studentName);
        grade.setClassGroup(classGroup);
        grade.setSubjectName(subject);
        grade.setGrade(grades);
        grade.setWeight_grades(1.0);
        grade.setNotes(note);
        grade.setTeacherUsername(teacherUsername);
        grade.setTeacherName(teacherName);
        grade.setCreatedTime(createdDateTime);

        gradeRepository.save(grade);
    }

    private void createAttendance(
            String studentUsername,
            String studentName,
            String classGroup,
            String subject,
            AttendanceStatus.Status status,
            String teacherUsername,
            String teacherName,
            String createdByTeacher
    ) {
        AttendanceRecords records = new AttendanceRecords();

        records.setAttendanceDate(LocalDate.now());
        records.setLessonIndex(1);
        records.setClassGroup(classGroup);
        records.setSubjectName(subject);
        records.setStudentUsername(studentUsername);
        records.setStudentName(studentName);
        records.setAttendanceStatus(status);
        records.setTeacherUsername(teacherUsername);
        records.setTeacherName(teacherName);
        records.setCreatedByTeacher(createdByTeacher);

        attendanceRepository.save(records);
    }

    private void createEvent(String type, String title, String description, String audience, String admin) {
        SchoolEvents events = new SchoolEvents();

        events.setEventType(type);
        events.setEventTitle(title);
        events.setEventDescription(description);
        events.setEventStartTime(LocalDateTime.now().plusDays(1));
        events.setEventEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        events.setLocation("J.Selye University");
        events.setAudience(audience);
        events.setCreatedByUser(admin);
        events.setSetRelatedTimetableId(0L);

        eventRepository.save(events);
    }
    private void createCanteenMenu(String audience, String ChefName, String dailyMenu,String weeklyMenu, String mainMeal, String secondMeal, String soup, String drinks, String deserts, Integer amount,String createdByChef){
       Canteen canteenMenu = new Canteen();

       canteenMenu.setAudience(audience);
       canteenMenu.setChefName(ChefName);
       canteenMenu.setDailyMenu(dailyMenu);
       canteenMenu.setWeeklyMenu(weeklyMenu);
       canteenMenu.setMainMeal(mainMeal);
       canteenMenu.setSecondMeal(secondMeal);
       canteenMenu.setSoup(soup);
       canteenMenu.setDrinks(drinks);
       canteenMenu.setDeserts(deserts);
       canteenMenu.setAmount(amount);
       canteenMenu.setCreatedByChef(createdByChef);

       canteenRepository.save(canteenMenu);
    }
}
