package org.backend.qedu.config;
import org.backend.qedu.entities.*;
import org.backend.qedu.model.*;
import org.backend.qedu.repo.*;
import org.backend.qedu.canteen.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.backend.qedu.service.QEduService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class DataSeed {
    private final UserRepo userRepository;
    private final TimeTableRepo timetableRepository;
    private final EventRepo eventRepository;
    private final GradeRepo gradeRepository;
    private final CanteenRepo canteenRepository;
    private final AttendanceRepo attendanceRepository;
    private final PasswordEncoder passwordEncoder;
    private User admin;
    @Bean
    CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            createUser("admin", "admin31", "Administrator", Roles.ADMIN, null);
            createUser("molnara", "molnara51", "Molnar Andras", Roles.TEACHER, "I.D");
            createUser("molnara", "tamas12", "Bona Tamas", Roles.TEACHER, "IV.D");
            createUser("molnara", "vigh14", "Vigh Laszlo", Roles.TEACHER, "III.D");
            createUser("molnara", "bence14", "Balog Bence", Roles.TEACHER, "II.D");
            createUser("kulcsara", "kulcsara21", "Kulcsar Adam", Roles.STUDENT, "I.D");
            createUser("violak", "violak43", "Viola Krisztian", Roles.STUDENT, "IV.D");
            createUser("mazant", "mazant52", "Mazan Tamas", Roles.STUDENT, "II.D");
            createUser("bbotond", "baloghb92", "Balogh Botond", Roles.STUDENT, "III.D");
            createUser("bonaa", "bonaa22", "Bona Adam", Roles.STUDENT, "II.D");
            createUser("chefp", "peterchef89", "Nagy Peter", Roles.CHEF, null);

            createTimetable(1, 1, "I.D", "Programozas", "tamas12", "Molnar Andras", "G-210", "08:50", "09:35");
            createTimetable(1, 1, "IV.D", "Programozas", "tamas12", "Bona Tamas", "G-210", "08:00", "8:45");
            createTimetable(4,5,"III.D","Adatstrukturak","vigh14","Vigh Laszlo","K-101","10:30","11:15");
            createTimetable(5,6,"II.D","Programozas","bence14","Balog Bence","DP005","12:15","13:00");

            createAttendance("kulcsara", "Kulcsar Adam", "I.D", "Programozas", AttendanceStatus.Status.PRESENT, "Molnar Andras","Molnar Andras");
            createAttendance("violak", "Viola Krisztian", "IV.D", "Programozas", AttendanceStatus.Status.LATE, "Bona Tamas","Bona Tamas");
            createAttendance("mazant", "Mazan Tamas", "II.D", "Adatstrukturak", AttendanceStatus.Status.ABSENT, "Balog Bence","Balog Bence");
            createAttendance("bbotond", "Balogh Botond", "III.D", "Matematika", AttendanceStatus.Status.EXCUSED, "Vigh Laszlo","Vigh Laszlo");
            createAttendance("bonaa", "Bona Adam", "II.D", "Adatstrukturak", AttendanceStatus.Status.PRESENT, "Balog Bence","Balog Bence");

            createGrade("kulcsara", "Kulcsar Adam", "I.D", "Programozas", 1, "Kituno", "molnara","Molnar Andras",LocalDateTime.now());
            createGrade("violak", "Viola Krisztian", "IV.D", "Programozas", 2, "Dicseretes", "molnara","Molnar Andras",LocalDateTime.now());
            createGrade("mazant", "Mazan Tamas", "II.D", "Adatstrukturak", 3, "Kozepes", "molnara","Molnar Andras",LocalDateTime.now());
            createGrade("bbotond", "Balogh Boton", "III.D", "Matematika", 3, "Kozepes", "molnara","Molnar Andras",LocalDateTime.now());
            createGrade("bonaa", "Bona Adam", "II.D", "Adatstrukturak", 4, "Elegseges", "molnara","Molnar Andras",LocalDateTime.now());

            createEvent("Helyettesites", "Helyettesites", "helyettesites tanaroknak", "mindenki",admin.getFullName());
            createEvent("Kirandulas", "Kirandulas", "egynapos kirandulas", "mindenki", admin.getFullName());
            createEvent("Szunidok","Szunido","Szunido az iskolaban","mindenki", admin.getFullName());
            createEvent("Helyettesites","Helyettesites","Helyettesites az orarendben","mindenki",admin.getFullName());

            createCanteenMenu("mindenki","Nagy Peter","Csibe rizsaval","Sertes kompottal","Leves","Masikfele","Paradicsomleves","Coca-Cola","Tiramisu",1,"Nagy Peter");
            createCanteenMenu("mindenki","Nagy Peter","Gordonblue","Zavtos hus","Leves","Masikfele","gombocleves","Sprite","Galuska",2,"Nagy Peter");
            createCanteenMenu("mindenki","Nagy Peter","GrillSajt","GrillHal","Leves","Masikfele","husleves","Light Cola","Sajttorta",1,"Nagy Peter");
        };
    }

    private void createUser(String username, String password, String fullName, Roles roles, String classGroup) {
        User user = new User();

        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRoles(roles);
        user.setClassGroups(classGroup);
        user.setEnabled(true);
        userRepository.save(user);
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
