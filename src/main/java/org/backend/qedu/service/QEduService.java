package org.backend.qedu.service;
import org.backend.qedu.canteen.Canteen;
import org.backend.qedu.dto.AttendanceRequest;
import org.backend.qedu.dto.EventRequest;
import org.backend.qedu.dto.StatisticsResponse;
import org.backend.qedu.dto.TimeTableRequest;
import org.backend.qedu.entities.*;
import org.backend.qedu.model.AttendanceStatus;
import org.backend.qedu.model.Roles;
import org.backend.qedu.canteen.CanteenRequest;
import org.backend.qedu.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QEduService {
    private final UserRepo userRepository;
    private final TimeTableRepo timetableRepository;
    private final EventRepo eventRepository;
    private final GradeRepo gradeRepository;
    private final AttendanceRepo attendanceRepository;
    private final CanteenRepo canteenRepository;

    public User currentUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByUserName(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not founded"));
    }

    public List<Timetable> getTimetable(User user) {
        return switch (user.getRoles()) {
            case ADMIN -> timetableRepository.findAllByOrderByDayOfWeekLessonIndex();
            case TEACHER ->
                    timetableRepository.findByTeacherUsernameOrderByDayOfWeekLessonIndex(user.getUserName());
            case STUDENT -> timetableRepository.findByClassGroupOrderByDayOfWeekLessonIndex(user.getClassGroups());
            case CHEF -> null;
        };
    }

    public Timetable createTimetable(TimeTableRequest request) {
        checkTimetable(request);

        Timetable timetable = new Timetable();
        timetable.setDayOfTheWeek(request.dayOfTheWeek());
        timetable.setLessonIdx(request.lessonInd());
        timetable.setClassGroups(request.classGroups());
        timetable.setSubjectNames(request.subjectNames());
        timetable.setTeacherUserName(request.teacherUsername());
        timetable.setTeacherName(request.teacherName());
        timetable.setClassRoomName(request.classRoomName());
        timetable.setLessonStartsAt(request.lessonStartsAt());
        timetable.setLessonEndsAt(request.lessonEndsAt());

        return timetableRepository.save(timetable);
    }

    private void checkTimetable(TimeTableRequest request) {
        if (request.dayOfTheWeek() < 1 || request.dayOfTheWeek() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid day");
        }

        if (request.lessonInd() < 1 || request.lessonInd() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid lesson number");
        }

        if (!request.lessonEndsAt().isAfter(request.lessonStartsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }

        User teacher = userRepository.findByUserName(request.teacherUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not found"));

        if (teacher.getRoles() != Roles.TEACHER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (timetableRepository.existsByDayOfWeekAndLessonIndexAndTeacherUsername(
                request.dayOfTheWeek(), request.lessonInd(), request.teacherUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (timetableRepository.existsByDayOfWeekAndLessonIndexAndClassGroup(
                request.dayOfTheWeek(), request.lessonInd(), request.classGroups())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (timetableRepository.existsByDayOfWeekAndLessonIndexAndRoomName(
                request.dayOfTheWeek(), request.lessonInd(), request.classRoomName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    public List<SchoolEvents> getEvents(User user) {
        return eventRepository.findByAudienceInAndStartsAtAfterOrderByStartsAtAsc(
                List.of("ALL", user.getRoles().name()),
                LocalDateTime.now()
        );
    }

    public SchoolEvents createEvent(EventRequest request, User creator) {
        SchoolEvents events = new SchoolEvents();

        events.setEventType(request.EventType().toUpperCase());
        events.setEventTitle(request.EventTitle());
        events.setEventDescription(request.EventDescription());
        events.setEventStartTime(request.EventStartsAt());
        events.setEventEndTime(request.EventEndsAt());
        events.setLocation(request.location());
        events.setAudience(request.audience().toUpperCase());
        events.setSetRelatedTimetableId(request.relatedTimetableId());
        events.setCreatedByUser(creator.getUserName());

        return eventRepository.save(events);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<AttendanceRecords> getAttendance(User user) {
        return switch (user.getRoles()) {
            case ADMIN -> attendanceRepository.findAllByOrderByAttendanceDateDesc();
            case TEACHER -> attendanceRepository.findByTeacherUsernameOrderByAttendanceDateDesc(user.getUserName());
            case CHEF -> null;
            case STUDENT -> attendanceRepository.findByStudentUsernameOrderByAttendanceDateDesc(user.getFullName());
        };
    }

    public AttendanceRecords createAttendance(AttendanceRequest request, User teacher) {
        if (teacher.getRoles() != Roles.TEACHER && teacher.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teacher or admin can create attendance");
        }

        AttendanceRecords records = new AttendanceRecords();
        records.setAttendanceDate(request.attendanceDate());
        records.setLessonIndex(request.lessonInx());
        records.setClassGroup(request.classGroup());
        records.setSubjectName(request.subjectNames());
        records.setStudentUsername(request.studentUserName());
        records.setStudentName(request.studentName());
        records.setAttendanceStatus(request.attendanceStatus());
        records.setTeacherName(teacher.getFullName());
        records.setCreatedByTeacher(String.valueOf(teacher.getCreatedAt()));
        return attendanceRepository.save(records);
    }

    public AttendanceRecords updateAttendance(Long id, AttendanceStatus.Status status) {
        AttendanceRecords record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));

        record.setAttendanceStatus(status);

        return attendanceRepository.save(record);
    }

    public StatisticsResponse getStatistics(User user) {
        return switch (user.getRoles()) {
            case STUDENT -> studentStatistics(user);
            case TEACHER -> teacherStatistics(user);
            case CHEF -> null;
            case ADMIN -> adminStatistics();
        };
    }

    private StatisticsResponse studentStatistics(User user) {
        Double average = gradeRepository.averageForStudent(user.getUserName());

        return new StatisticsResponse(
                Roles.STUDENT,
                average == null ? 0.0 : average,
                gradeRepository.countByStudentUsername(user.getUserName()),
                attendanceRepository.countByStudentUsernameAndStatus(user.getUserName(), AttendanceStatus.Status.ABSENT),
                attendanceRepository.countByStudentUsernameAndStatus(user.getUserName(), AttendanceStatus.Status.LATE),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private StatisticsResponse teacherStatistics(User user) {
        Double average = gradeRepository.averageForTeacher(user.getUserName());

        long taughtClasses = timetableRepository
                .findByTeacherUsernameOrderByDayOfWeekLessonIndex(user.getUserName())
                .stream()
                .map(Timetable::getClassGroups)
                .distinct()
                .count();

        return new StatisticsResponse(
                Roles.TEACHER,
                average == null ? 0.0 : average,
                null,
                attendanceRepository.countByTeacherUsernameAndStatus(user.getUserName(), AttendanceStatus.Status.ABSENT),
                attendanceRepository.countByTeacherUsernameAndStatus(user.getUserName(), AttendanceStatus.Status.LATE),
                null,
                null,
                null,
                taughtClasses,
                null,
                null
        );
    }

    private StatisticsResponse adminStatistics() {
        Double average = gradeRepository.averageForSchool();

        return new StatisticsResponse(
                Roles.ADMIN,
                average == null ? 0.0 : average,
                null,
                attendanceRepository.countByStatus(AttendanceStatus.Status.ABSENT),
                attendanceRepository.countByStatus(AttendanceStatus.Status.LATE),
                userRepository.countByRoles(Roles.STUDENT),
                userRepository.countByRoles(Roles.TEACHER),
                userRepository.countByRoles(Roles.ADMIN),
                null,
                timetableRepository.countByDayOfWeek(LocalDate.now().getDayOfWeek().getValue()),
                eventRepository.countByStartsAtBetween(LocalDateTime.now(), LocalDateTime.now().plusDays(7))
        );
    }
    public List<Canteen> getCanteenList(){
        return canteenRepository.findAllByOrderMenuAndDate();
    }
    public Canteen createCanteenMenu(CanteenRequest request, User Chef){
        //check if the user is chef, only chef can manage canteen.

        if(Chef.getRoles() != Roles.CHEF){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Only Chef can manage canteen menus.");
        }
        if(canteenRepository.existsMenuDate(request.getmenuDateTime())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Menu already exists for this date");
        }

        Canteen canteenMenu = new Canteen();

        canteenMenu.setMainMeal(request.getMainMeal());
        canteenMenu.setSecondMeal(request.getSecondMeal());
        canteenMenu.setSoup(request.getSoup());
        canteenMenu.setDrinks(request.getDrinks());
        canteenMenu.setDailyMenu(request.getDailyMenu());
        canteenMenu.setWeeklyMenu(request.getWeeklyMenu());
        canteenMenu.setAudience(request.getAudience());
        canteenMenu.setDeserts(request.getDesserts());
        canteenMenu.setAmount(request.getAmount());
        canteenMenu.setChefName(Chef.getFullName());
        canteenMenu.setCreatedByChef(Chef.getUserName());

        return canteenRepository.save(canteenMenu);
    }
    public void DeleteCanteenMenu(Long Id, User Chef){

        //check if the user is chef, only chef can manage canteen.
        if(Chef.getRoles() != Roles.CHEF){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Only Chef can delete canteen menus.");
        }
        canteenRepository.deleteById(Id);
    }
}
