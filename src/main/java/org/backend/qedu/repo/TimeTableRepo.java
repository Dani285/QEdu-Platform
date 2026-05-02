package org.backend.qedu.repo;
import org.backend.qedu.entities.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TimeTableRepo extends JpaRepository<Timetable, Long>{
    boolean existsByDayOfWeekAndLessonIndexAndTeacherUsername(
            Integer dayOfWeek,
            Integer lessonIndex,
            String teacherUsername
    );

    boolean existsByDayOfWeekAndLessonIndexAndClassGroup(
            Integer dayOfWeek,
            Integer lessonIndex,
            String classGroup
    );

    boolean existsByDayOfWeekAndLessonIndexAndRoomName(
            Integer dayOfWeek,
            Integer lessonIndex,
            String roomName
    );

    List<Timetable> findByTeacherUsernameOrderByDayOfWeekLessonIndex(String teacherUsername);

    List<Timetable> findByClassGroupOrderByDayOfWeekLessonIndex(String classGroup);

    List<Timetable> findAllByOrderByDayOfWeekLessonIndex();

    long countByDayOfWeek(Integer dayOfWeek);
}
