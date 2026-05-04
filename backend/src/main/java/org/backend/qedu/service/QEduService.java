package org.backend.qedu.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backend.qedu.canteen.Canteen;
import org.backend.qedu.canteen.CanteenRequest;
import org.backend.qedu.dto.*;
import org.backend.qedu.dto.AttendanceRequest;
import org.backend.qedu.dto.ClassSubjectRequest;
import org.backend.qedu.dto.ClassSubjectResponse;
import org.backend.qedu.dto.EventRequest;
import org.backend.qedu.dto.ExamRequest;
import org.backend.qedu.dto.GradeCreateRequest;
import org.backend.qedu.dto.GradeResponse;
import org.backend.qedu.dto.GradeUpdateRequest;
import org.backend.qedu.dto.MessageThreadRequest;
import org.backend.qedu.dto.ProjectRequest;
import org.backend.qedu.dto.StatisticsResponse;
import org.backend.qedu.dto.StudyMaterialRequest;
import org.backend.qedu.dto.TimeTableRequest;
import org.backend.qedu.dto.UserPatchRequest;
import org.backend.qedu.entities.*;
import org.backend.qedu.model.AttendanceStatus;
import org.backend.qedu.model.Roles;
import org.backend.qedu.repo.*;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.*;
import org.backend.qedu.entities.*;
import org.backend.qedu.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QEduService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepository;
    private final TimetableRepo timetableRepository;
    private final EventRepo eventRepository;
    private final GradeRepo gradeRepository;
    private final AttendanceRepo attendanceRepository;
    private final CanteenRepo canteenRepository;
    private final ClassSubjectRepo classSubjectRepository;
    private final StudentSubjectEnrollmentRepo enrollmentRepository;
    private final StudyMaterialRepo studyMaterialRepository;
    private final MessageThreadRepo messageThreadRepository;
    private final QEduProjectRepo qeduProjectRepository;
    private final QEduExamRepo qeduExamRepository;
    private final ObjectMapper objectMapper;

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
        timetable.setLessonIdx(request.lessonIdx());
        timetable.setClassGroups(request.classGroups());
        timetable.setSubjectNames(request.subjectNames());
        timetable.setTeacherUserName(request.teacherUserName());
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

        if (request.lessonIdx() < 1 || request.lessonIdx() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid lesson number");
        }

        if (!request.lessonEndsAt().isAfter(request.lessonStartsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }

        User teacher = userRepository.findByUserName(request.teacherUserName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not found"));

        if (teacher.getRoles() != Roles.TEACHER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (timetableRepository.existsByDayOfWeekAndLessonIndexAndTeacherUsername(
                request.dayOfTheWeek(), request.lessonIdx(), request.teacherUserName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (timetableRepository.existsByDayOfWeekAndLessonIndexAndClassGroup(
                request.dayOfTheWeek(), request.lessonIdx(), request.classGroups())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (timetableRepository.existsByDayOfWeekAndLessonIndexAndRoomName(
                request.dayOfTheWeek(), request.lessonIdx(), request.classRoomName())) {
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
            case CHEF -> List.of();
            case STUDENT -> attendanceRepository.findByStudentUsernameOrderByAttendanceDateDesc(user.getUserName());
        };
    }

    public AttendanceRecords createAttendance(AttendanceRequest request, User teacher) {
        if (teacher.getRoles() != Roles.TEACHER && teacher.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teacher or admin can create attendance");
        }

        User studentUser = userRepository.findByUserName(request.studentUserName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found"));
        if (teacher.getRoles() == Roles.TEACHER) {
            assertStudentEnrolledForTeacherSubject(
                    teacher.getUserName(), request.classGroup(), request.subjectNames(), studentUser.getID());
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
        records.setTeacherUsername(teacher.getUserName());
        records.setCreatedByTeacher(
                teacher.getFullName() != null && !teacher.getFullName().isBlank()
                        ? teacher.getFullName()
                        : teacher.getUserName());
        return attendanceRepository.save(records);
    }

    public AttendanceRecords updateAttendance(Long id, AttendanceStatus.Status status) {
        AttendanceRecords record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));

        record.setAttendanceStatus(status);

        return attendanceRepository.save(record);
    }

    public void deleteAttendance(User actor, Long id) {
        AttendanceRecords record = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.TEACHER) {
            boolean own = Objects.equals(record.getTeacherUsername(), actor.getUserName());
            boolean teaches = teacherTeachesClassSubject(actor.getUserName(), record.getClassGroup(), record.getSubjectName());
            if (!own && !teaches) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        attendanceRepository.deleteById(id);
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
                attendanceRepository.countByStudentUsernameAndAttendanceStatus(user.getUserName(), AttendanceStatus.Status.ABSENT),
                attendanceRepository.countByStudentUsernameAndAttendanceStatus(user.getUserName(), AttendanceStatus.Status.LATE),
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
                attendanceRepository.countByTeacherUsernameAndAttendanceStatus(user.getUserName(), AttendanceStatus.Status.ABSENT),
                attendanceRepository.countByTeacherUsernameAndAttendanceStatus(user.getUserName(), AttendanceStatus.Status.LATE),
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
                attendanceRepository.countByAttendanceStatus(AttendanceStatus.Status.ABSENT),
                attendanceRepository.countByAttendanceStatus(AttendanceStatus.Status.LATE),
                userRepository.countByRoles(Roles.STUDENT),
                userRepository.countByRoles(Roles.TEACHER),
                userRepository.countByRoles(Roles.ADMIN),
                null,
                timetableRepository.countByDayOfWeek(LocalDate.now().getDayOfWeek().getValue()),
                eventRepository.countByStartsAtBetween(LocalDateTime.now(), LocalDateTime.now().plusDays(7))
        );
    }
    public List<Canteen> getCanteenList(){
        return canteenRepository.findAllByOrderByIDDesc();
    }
    public Canteen createCanteenMenu(CanteenRequest request, User Chef){
        //check if the user is chef, only chef can manage canteen.

        if(Chef.getRoles() != Roles.CHEF){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Only Chef can manage canteen menus.");
        }
        if(canteenRepository.existsByDailyMenuAndWeeklyMenu(request.getDailyMenu(),request.getWeeklyMenu())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Weekly and daily menu exist already");
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

    /* ========= Users (admin) ========= */

    public UserDtos.UserDto registerNewUser(UserDtos.RegisterRequest request) {
        if (userRepository.findByUserName(request.userName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "UserName already exist");
        }
        User user = new User();
        user.setUserName(request.userName());
        user.setPassword(passwordEncoder.encode(request.Password()));
        user.setFullName(request.fullName());
        user.setClassGroups(request.classGroup());
        user.setRoles(request.role());
        user.setEnabled(true);
        userRepository.save(user);
        enrollNewStudentInClassAssignments(user);
        return UserDtos.UserDto.from(user);
    }

    public List<UserDtos.UserDto> listAllUsers() {
        return userRepository.findAll().stream().map(UserDtos.UserDto::from).toList();
    }

    public UserDtos.UserDto getUserById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return UserDtos.UserDto.from(u);
    }

    @Transactional
    public UserDtos.UserDto patchUser(Long id, UserPatchRequest req) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Roles oldRole = u.getRoles();
        String oldClass = u.getClassGroups();

        if (req.enabled() != null) {
            u.setEnabled(req.enabled());
        }
        if (req.role() != null) {
            u.setRoles(req.role());
        }
        if (req.classGroup() != null) {
            String cg = req.classGroup().trim();
            if (cg.isEmpty() || "-".equals(cg)) {
                u.setClassGroups(null);
            } else {
                u.setClassGroups(cg);
            }
        }

        u.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(u);

        boolean classPatched = req.classGroup() != null;
        String newClass = saved.getClassGroups();
        boolean classChanged = classPatched && !Objects.equals(
                oldClass == null ? "" : oldClass.trim(),
                newClass == null ? "" : newClass.trim());
        boolean roleChanged = req.role() != null && oldRole != saved.getRoles();

        if (oldRole == Roles.STUDENT) {
            if (classChanged || (roleChanged && saved.getRoles() != Roles.STUDENT)) {
                enrollmentRepository.deleteAllByStudentId(saved.getID());
            }
        }
        if (saved.getRoles() == Roles.STUDENT && newClass != null && !newClass.isBlank()) {
            if (oldRole == Roles.STUDENT && classChanged) {
                enrollNewStudentInClassAssignments(saved);
            } else if (roleChanged && saved.getRoles() == Roles.STUDENT && oldRole != Roles.STUDENT) {
                enrollNewStudentInClassAssignments(saved);
            }
        }

        return UserDtos.UserDto.from(saved);
    }

    @Transactional
    public void deleteUser(Long id, User actor) {
        if (actor.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (Objects.equals(target.getID(), actor.getID())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete your own account");
        }
        if (target.getRoles() == Roles.ADMIN && userRepository.countByRoles(Roles.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the last admin account");
        }
        enrollmentRepository.deleteAllByStudentId(target.getID());
        userRepository.delete(target);
    }

    /* ========= Grades ========= */

    public List<GradeResponse> listGrades(User viewer, String classGroup, String subjectName, String studentUsername) {
        return switch (viewer.getRoles()) {
            case STUDENT -> gradeRepository.findByStudentUsernameOrderByCreatedTimeDesc(viewer.getUserName()).stream()
                    .map(GradeResponse::from)
                    .toList();
            case TEACHER -> {
                /* Ha megvan az osztály + tantárgy: minden jegy ahhoz a kontextushoz (nem csak a saját teacherUsername). */
                if (classGroup != null && subjectName != null) {
                    if (!teacherTeachesClassSubject(viewer.getUserName(), classGroup, subjectName)) {
                        yield List.of();
                    }
                    List<GradeRecords> forClassSubject = gradeRepository.findForClassGroupAndSubject(classGroup, subjectName).stream()
                            .filter(g -> studentUsername == null || Objects.equals(g.getStudentUsername(), studentUsername))
                            .toList();
                    Optional<ClassSubjectAssignment> assignment = classSubjectRepository.findForTeacherClassAndSubject(
                            viewer.getUserName(), classGroup, subjectName);
                    List<GradeRecords> scoped = forClassSubject;
                    if (assignment.isPresent()) {
                        Set<String> allowedUsernames = new HashSet<>(
                                enrollmentRepository.findStudentUsernamesByClassSubjectId(assignment.get().getId()));
                        scoped = forClassSubject.stream()
                                .filter(g -> allowedUsernames.contains(g.getStudentUsername()))
                                .toList();
                    }
                    yield scoped.stream().map(GradeResponse::from).toList();
                }
                yield gradeRepository.findByTeacherUsernameOrderByCreatedTimeDesc(viewer.getUserName()).stream()
                        .filter(g -> classGroup == null || Objects.equals(g.getClassGroup(), classGroup))
                        .filter(g -> subjectName == null || g.getSubjectName().equalsIgnoreCase(subjectName))
                        .filter(g -> studentUsername == null || Objects.equals(g.getStudentUsername(), studentUsername))
                        .map(GradeResponse::from)
                        .toList();
            }
            case ADMIN -> gradeRepository.findAllByOrderByCreatedTimeDesc().stream()
                    .filter(g -> classGroup == null || Objects.equals(g.getClassGroup(), classGroup))
                    .filter(g -> subjectName == null || g.getSubjectName().equalsIgnoreCase(subjectName))
                    .filter(g -> studentUsername == null || Objects.equals(g.getStudentUsername(), studentUsername))
                    .map(GradeResponse::from)
                    .toList();
            case CHEF -> throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        };
    }

    private boolean teacherTeachesClassSubject(String teacherUsername, String classGroup, String subjectName) {
        return classSubjectRepository.findByTeacherUsernameOrderByClassIdAsc(teacherUsername).stream()
                .anyMatch(a -> Objects.equals(a.getClassName(), classGroup)
                        && a.getSubjectName().equalsIgnoreCase(subjectName));
    }

    private boolean teacherMayAccessGradeRecord(User actor, GradeRecords g) {
        if (actor.getRoles() != Roles.TEACHER) {
            return true;
        }
        return Objects.equals(g.getTeacherUsername(), actor.getUserName())
                || teacherTeachesClassSubject(actor.getUserName(), g.getClassGroup(), g.getSubjectName());
    }

    public GradeResponse createGrade(User actor, GradeCreateRequest request) {
        if (actor.getRoles() != Roles.TEACHER && actor.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        User student = userRepository.findByUserName(request.studentUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found"));
        if (student.getRoles() != Roles.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target user is not a student");
        }
        if (actor.getRoles() == Roles.TEACHER) {
            assertStudentEnrolledForTeacherSubject(
                    actor.getUserName(), request.classGroup(), request.subjectName(), student.getID());
        }
        GradeRecords g = new GradeRecords();
        g.setStudentUsername(request.studentUsername());
        g.setStudentName(request.studentName());
        g.setClassGroup(request.classGroup());
        g.setSubjectName(request.subjectName());
        g.setGrade(request.grade());
        g.setNotes(request.notes());
        g.setWeight_grades(request.weightGrades());
        g.setTeacherUsername(actor.getUserName());
        g.setTeacherName(actor.getFullName());
        g.setCreatedTime(LocalDateTime.now());
        return GradeResponse.from(gradeRepository.save(g));
    }

    public GradeResponse updateGrade(User actor, Long gradeId, GradeUpdateRequest request) {
        GradeRecords g = gradeRepository.findById(gradeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !teacherMayAccessGradeRecord(actor, g)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.CHEF || actor.getRoles() == Roles.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (request.grade() != null) {
            g.setGrade(request.grade());
        }
        if (request.notes() != null) {
            g.setNotes(request.notes());
        }
        if (request.weightGrades() != null) {
            g.setWeight_grades(request.weightGrades());
        }
        return GradeResponse.from(gradeRepository.save(g));
    }

    public void deleteGrade(User actor, Long gradeId) {
        GradeRecords g = gradeRepository.findById(gradeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !teacherMayAccessGradeRecord(actor, g)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        gradeRepository.deleteById(gradeId);
    }

    /* ========= Class / subject assignments ========= */

    public List<ClassSubjectResponse> listClassSubjects() {
        return classSubjectRepository.findAllByOrderByClassIdAscSubjectNameAsc().stream()
                .map(a -> ClassSubjectResponse.from(a, enrollmentRepository.countByClassSubject_Id(a.getId())))
                .toList();
    }

    public List<UserDtos.UserDto> listStudentsForDirectory(User viewer, String classId, String subjectId) {
        if (classId != null && !classId.isBlank() && subjectId != null && !subjectId.isBlank()) {
            ClassSubjectAssignment a = classSubjectRepository.findByClassIdAndSubjectId(classId, subjectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class-subject assignment not found"));
            if (viewer.getRoles() == Roles.TEACHER) {
                if (!Objects.equals(a.getTeacherUsername(), viewer.getUserName())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            } else if (viewer.getRoles() != Roles.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            List<Long> ids = enrollmentRepository.findStudentIdsByClassSubjectId(a.getId());
            if (ids.isEmpty()) {
                return List.of();
            }
            return userRepository.findAllById(ids).stream()
                    .filter(u -> u.getRoles() == Roles.STUDENT)
                    .map(UserDtos.UserDto::from)
                    .sorted(Comparator.comparing(UserDtos.UserDto::fullName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
        }
        return listStudentsForDirectoryUnscoped(viewer);
    }

    private List<UserDtos.UserDto> listStudentsForDirectoryUnscoped(User viewer) {
        return switch (viewer.getRoles()) {
            case ADMIN -> userRepository.findByRoles(Roles.STUDENT).stream()
                    .map(UserDtos.UserDto::from)
                    .toList();
            case TEACHER -> {
                Set<String> classNames = classSubjectRepository
                        .findByTeacherUsernameOrderByClassIdAsc(viewer.getUserName())
                        .stream()
                        .map(ClassSubjectAssignment::getClassName)
                        .collect(Collectors.toSet());
                yield userRepository.findByRoles(Roles.STUDENT).stream()
                        .filter(u -> u.getClassGroups() != null && classNames.contains(u.getClassGroups()))
                        .map(UserDtos.UserDto::from)
                        .toList();
            }
            case STUDENT -> {
                String cg = viewer.getClassGroups();
                if (cg == null) {
                    yield List.of();
                }
                yield userRepository.findByRolesAndClassGroups(Roles.STUDENT, cg).stream()
                        .map(UserDtos.UserDto::from)
                        .toList();
            }
            case CHEF -> List.of();
        };
    }

    public ClassSubjectResponse createClassSubject(ClassSubjectRequest req) {
        User teacher = userRepository.findByUserName(req.teacherUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not found"));
        if (teacher.getRoles() != Roles.TEACHER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a teacher");
        }
        ClassSubjectAssignment a = new ClassSubjectAssignment();
        a.setClassId(req.classId());
        a.setClassName(req.className());
        a.setSubjectId(req.subjectId());
        a.setSubjectName(req.subjectName());
        a.setTeacherUsername(teacher.getUserName());
        a.setTeacherName(teacher.getFullName());
        ClassSubjectAssignment saved = classSubjectRepository.save(a);
        enrollAllStudentsForAssignment(saved);
        return ClassSubjectResponse.from(saved, enrollmentRepository.countByClassSubject_Id(saved.getId()));
    }

    public ClassSubjectResponse updateClassSubject(Long id, ClassSubjectRequest req) {
        ClassSubjectAssignment a = classSubjectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User teacher = userRepository.findByUserName(req.teacherUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not found"));
        if (teacher.getRoles() != Roles.TEACHER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a teacher");
        }
        String oldClassName = a.getClassName();
        a.setClassId(req.classId());
        a.setClassName(req.className());
        a.setSubjectId(req.subjectId());
        a.setSubjectName(req.subjectName());
        a.setTeacherUsername(teacher.getUserName());
        a.setTeacherName(teacher.getFullName());
        ClassSubjectAssignment saved = classSubjectRepository.save(a);
        if (!Objects.equals(oldClassName, saved.getClassName())) {
            enrollmentRepository.deleteByClassSubject_Id(saved.getId());
            enrollAllStudentsForAssignment(saved);
        }
        return ClassSubjectResponse.from(saved, enrollmentRepository.countByClassSubject_Id(saved.getId()));
    }

    public void deleteClassSubject(Long id) {
        if (!classSubjectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        enrollmentRepository.deleteByClassSubject_Id(id);
        classSubjectRepository.deleteById(id);
    }

    public List<UserDtos.UserDto> listEnrolledStudents(Long classSubjectId, User viewer) {
        ClassSubjectAssignment a = classSubjectRepository.findById(classSubjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (viewer.getRoles() == Roles.TEACHER) {
            if (!Objects.equals(a.getTeacherUsername(), viewer.getUserName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else if (viewer.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<Long> ids = enrollmentRepository.findStudentIdsByClassSubjectId(classSubjectId);
        if (ids.isEmpty()) {
            return List.of();
        }
        return userRepository.findAllById(ids).stream()
                .filter(u -> u.getRoles() == Roles.STUDENT)
                .map(UserDtos.UserDto::from)
                .sorted(Comparator.comparing(UserDtos.UserDto::fullName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    public void replaceEnrollments(Long classSubjectId, List<Long> studentUserIds) {
        ClassSubjectAssignment a = classSubjectRepository.findById(classSubjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Set<Long> allowed = userRepository.findByRolesAndClassGroups(Roles.STUDENT, a.getClassName()).stream()
                .map(User::getID)
                .collect(Collectors.toSet());
        List<Long> distinctIds = studentUserIds.stream().distinct().toList();
        for (Long sid : distinctIds) {
            if (!allowed.contains(sid)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not in class roster: " + sid);
            }
        }
        enrollmentRepository.deleteByClassSubject_Id(classSubjectId);
        for (Long sid : distinctIds) {
            User s = userRepository.findById(sid).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
            if (s.getRoles() != Roles.STUDENT) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a student: " + sid);
            }
            StudentSubjectEnrollment e = new StudentSubjectEnrollment();
            e.setStudent(s);
            e.setClassSubject(a);
            enrollmentRepository.save(e);
        }
    }

    private void enrollAllStudentsForAssignment(ClassSubjectAssignment a) {
        userRepository.findByRolesAndClassGroups(Roles.STUDENT, a.getClassName()).forEach(u -> {
            if (!enrollmentRepository.existsByClassSubject_IdAndStudent_ID(a.getId(), u.getID())) {
                StudentSubjectEnrollment e = new StudentSubjectEnrollment();
                e.setStudent(u);
                e.setClassSubject(a);
                enrollmentRepository.save(e);
            }
        });
    }

    private void enrollNewStudentInClassAssignments(User u) {
        if (u.getRoles() != Roles.STUDENT || u.getClassGroups() == null || u.getClassGroups().isBlank()) {
            return;
        }
        classSubjectRepository.findAllByOrderByClassIdAscSubjectNameAsc().stream()
                .filter(a -> Objects.equals(a.getClassName(), u.getClassGroups()))
                .forEach(a -> {
                    if (!enrollmentRepository.existsByClassSubject_IdAndStudent_ID(a.getId(), u.getID())) {
                        StudentSubjectEnrollment e = new StudentSubjectEnrollment();
                        e.setStudent(u);
                        e.setClassSubject(a);
                        enrollmentRepository.save(e);
                    }
                });
    }

    private void assertStudentEnrolledForTeacherSubject(
            String teacherUsername, String className, String subjectName, Long studentId
    ) {
        Optional<ClassSubjectAssignment> a = classSubjectRepository.findForTeacherClassAndSubject(
                teacherUsername, className, subjectName);
        if (a.isEmpty()) {
            return;
        }
        if (!enrollmentRepository.existsByClassSubject_IdAndStudent_ID(a.get().getId(), studentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is not enrolled in this subject");
        }
    }

    public List<StudyMaterial> listMaterials(User viewer) {
        return switch (viewer.getRoles()) {
            case STUDENT -> {
                String cls = viewer.getClassGroups();
                if (cls == null) {
                    yield List.of();
                }
                yield studyMaterialRepository.findByClassIdOrderByUpdatedAtDesc(cls);
            }
            case TEACHER -> studyMaterialRepository.findByTeacherUsernameOrderByUpdatedAtDesc(viewer.getUserName());
            case ADMIN -> studyMaterialRepository.findAllByOrderByUpdatedAtDesc();
            case CHEF -> List.of();
        };
    }

    public StudyMaterial createMaterial(User teacher, StudyMaterialRequest req) {
        if (teacher.getRoles() != Roles.TEACHER && teacher.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        StudyMaterial m = new StudyMaterial();
        fillMaterial(m, req, teacher.getUserName());
        m.setUpdatedAt(LocalDateTime.now());
        return studyMaterialRepository.save(m);
    }

    public StudyMaterial updateMaterial(User actor, Long id, StudyMaterialRequest req) {
        StudyMaterial m = studyMaterialRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !m.getTeacherUsername().equals(actor.getUserName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.CHEF || actor.getRoles() == Roles.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        fillMaterial(m, req, m.getTeacherUsername());
        m.setUpdatedAt(LocalDateTime.now());
        return studyMaterialRepository.save(m);
    }

    public void deleteMaterial(User actor, Long id) {
        StudyMaterial m = studyMaterialRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !m.getTeacherUsername().equals(actor.getUserName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        studyMaterialRepository.deleteById(id);
    }

    private static void fillMaterial(StudyMaterial m, StudyMaterialRequest req, String teacherUsername) {
        m.setTitle(req.title());
        m.setClassId(req.classId());
        m.setClassName(req.className());
        m.setSubjectId(req.subjectId());
        m.setSubjectName(req.subjectName());
        m.setMaterialType(req.materialType());
        m.setStatus(req.status());
        m.setValidFrom(req.validFrom());
        m.setValidTo(req.validTo());
        m.setTeacherUsername(teacherUsername);
    }

    public List<MessageThread> listMessageThreads(User viewer) {
        List<MessageThread> all = messageThreadRepository.findAllByOrderByCreatedAtDesc();
        if (viewer.getRoles() == Roles.ADMIN) {
            return all;
        }
        return all.stream().filter(t -> isMessageVisibleTo(viewer, t)).toList();
    }

    private boolean isMessageVisibleTo(User viewer, MessageThread t) {
        try {
            List<String> aud = objectMapper.readValue(t.getAudienceJson(), new TypeReference<>() {
            });
            String role = viewer.getRoles().name();
            boolean audOk = aud.stream().anyMatch(a -> "ALL".equalsIgnoreCase(a) || role.equalsIgnoreCase(a));
            if (!audOk) {
                return false;
            }
            String ct = t.getClassTargetsJson();
            if (ct == null || ct.isBlank() || "[]".equals(ct.trim())) {
                return true;
            }
            List<String> targets = objectMapper.readValue(ct, new TypeReference<>() {
            });
            if (targets.isEmpty()) {
                return true;
            }
            if (viewer.getRoles() == Roles.STUDENT) {
                String cls = viewer.getClassGroups();
                return cls != null && targets.stream().anyMatch(x -> x.equalsIgnoreCase(cls));
            }
            return viewer.getRoles() == Roles.TEACHER || viewer.getRoles() == Roles.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    public MessageThread createMessageThread(User author, MessageThreadRequest req) {
        if (author.getRoles() != Roles.TEACHER && author.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        MessageThread m = new MessageThread();
        m.setTitle(req.title());
        m.setLastMessage(req.lastMessage());
        m.setAuthorUsername(author.getUserName());
        m.setAuthorRole(author.getRoles().name());
        m.setAudienceJson(req.audienceJson());
        m.setClassTargetsJson(req.classTargetsJson() == null ? "[]" : req.classTargetsJson());
        m.setCreatedAt(LocalDateTime.now());
        return messageThreadRepository.save(m);
    }

    public MessageThread updateMessageThread(User actor, Long id, MessageThreadRequest req) {
        MessageThread m = messageThreadRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!m.getAuthorUsername().equals(actor.getUserName()) && actor.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        m.setTitle(req.title());
        m.setLastMessage(req.lastMessage());
        m.setAudienceJson(req.audienceJson());
        m.setClassTargetsJson(req.classTargetsJson() == null ? "[]" : req.classTargetsJson());
        return messageThreadRepository.save(m);
    }

    public void deleteMessageThread(User actor, Long id) {
        MessageThread m = messageThreadRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!m.getAuthorUsername().equals(actor.getUserName()) && actor.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        messageThreadRepository.deleteById(id);
    }

    /* ========= Projects ========= */

    public List<QEduProject> listProjects(User viewer) {
        return switch (viewer.getRoles()) {
            case STUDENT -> {
                String cls = viewer.getClassGroups();
                if (cls == null) {
                    yield List.of();
                }
                yield qeduProjectRepository.findByClassIdOrderByUpdatedAtDesc(cls);
            }
            case TEACHER -> qeduProjectRepository.findByTeacherUsernameOrderByUpdatedAtDesc(viewer.getUserName());
            case ADMIN -> qeduProjectRepository.findAllByOrderByUpdatedAtDesc();
            case CHEF -> List.of();
        };
    }

    public QEduProject createProject(User teacher, ProjectRequest req) {
        if (teacher.getRoles() != Roles.TEACHER && teacher.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        QEduProject p = new QEduProject();
        fillProject(p, req);
        p.setTeacherUsername(teacher.getUserName());
        p.setUpdatedAt(LocalDateTime.now());
        return qeduProjectRepository.save(p);
    }

    public QEduProject updateProject(User actor, Long id, ProjectRequest req) {
        QEduProject p = qeduProjectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !p.getTeacherUsername().equals(actor.getUserName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        fillProject(p, req);
        p.setUpdatedAt(LocalDateTime.now());
        return qeduProjectRepository.save(p);
    }

    public void deleteProject(User actor, Long id) {
        QEduProject p = qeduProjectRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !p.getTeacherUsername().equals(actor.getUserName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        qeduProjectRepository.deleteById(id);
    }

    private static void fillProject(QEduProject p, ProjectRequest req) {
        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setClassId(req.classId());
        p.setClassName(req.className());
        p.setSubjectId(req.subjectId());
        p.setSubjectName(req.subjectName());
        p.setStatus(req.status());
        p.setStartsAt(req.startsAt());
        p.setDeadline(req.deadline());
        p.setProgress(req.progress() != null ? req.progress() : 0);
    }


    public List<QEduExam> listExams(User viewer) {
        return switch (viewer.getRoles()) {
            case STUDENT -> {
                String cls = viewer.getClassGroups();
                if (cls == null) {
                    yield List.of();
                }
                yield qeduExamRepository.findByClassIdOrderByExamDateAsc(cls).stream()
                        .filter(e -> !"draft".equalsIgnoreCase(e.getStatus()))
                        .toList();
            }
            case TEACHER -> qeduExamRepository.findByTeacherUsernameOrderByExamDateAsc(viewer.getUserName());
            case ADMIN -> qeduExamRepository.findAllByOrderByExamDateAsc();
            case CHEF -> List.of();
        };
    }

    public QEduExam createExam(User teacher, ExamRequest req) {
        if (teacher.getRoles() != Roles.TEACHER && teacher.getRoles() != Roles.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        QEduExam e = new QEduExam();
        fillExam(e, req);
        e.setTeacherUsername(teacher.getUserName());
        return qeduExamRepository.save(e);
    }

    public QEduExam updateExam(User actor, Long id, ExamRequest req) {
        QEduExam e = qeduExamRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !e.getTeacherUsername().equals(actor.getUserName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        fillExam(e, req);
        return qeduExamRepository.save(e);
    }

    public void deleteExam(User actor, Long id) {
        QEduExam e = qeduExamRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (actor.getRoles() == Roles.TEACHER && !e.getTeacherUsername().equals(actor.getUserName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (actor.getRoles() == Roles.STUDENT || actor.getRoles() == Roles.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        qeduExamRepository.deleteById(id);
    }

    private static void fillExam(QEduExam e, ExamRequest req) {
        e.setTitle(req.title());
        e.setClassId(req.classId());
        e.setClassName(req.className());
        e.setSubjectId(req.subjectId());
        e.setSubjectName(req.subjectName());
        e.setMaxPoints(req.maxPoints());
        e.setExamDate(req.examDate());
        e.setStatus(req.status());
        e.setKind(req.kind());
        e.setGradingJson(req.gradingJson());
        e.setQuestionsJson(req.questionsJson());
    }
}

