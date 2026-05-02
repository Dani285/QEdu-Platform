package org.backend.qedu.repo;

import org.backend.qedu.entities.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimetableRepo extends JpaRepository<Timetable, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM Timetable t
            WHERE t.dayOfTheWeek = :dayOfWeek
              AND t.lessonIdx = :lessonIndex
              AND t.teacherUserName = :teacherUsername
            """)
    boolean existsByDayOfWeekAndLessonIndexAndTeacherUsername(
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("lessonIndex") Integer lessonIndex,
            @Param("teacherUsername") String teacherUsername
    );

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM Timetable t
            WHERE t.dayOfTheWeek = :dayOfWeek
              AND t.lessonIdx = :lessonIndex
              AND t.classGroups = :classGroup
            """)
    boolean existsByDayOfWeekAndLessonIndexAndClassGroup(
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("lessonIndex") Integer lessonIndex,
            @Param("classGroup") String classGroup
    );

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM Timetable t
            WHERE t.dayOfTheWeek = :dayOfWeek
              AND t.lessonIdx = :lessonIndex
              AND t.classRoomName = :roomName
            """)
    boolean existsByDayOfWeekAndLessonIndexAndRoomName(
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("lessonIndex") Integer lessonIndex,
            @Param("roomName") String roomName
    );

    @Query("""
            SELECT t
            FROM Timetable t
            WHERE t.teacherUserName = :teacherUsername
            ORDER BY t.dayOfTheWeek ASC, t.lessonIdx ASC
            """)
    List<Timetable> findByTeacherUsernameOrderByDayOfWeekLessonIndex(
            @Param("teacherUsername") String teacherUsername
    );

    @Query("""
            SELECT t
            FROM Timetable t
            WHERE t.classGroups = :classGroup
            ORDER BY t.dayOfTheWeek ASC, t.lessonIdx ASC
            """)
    List<Timetable> findByClassGroupOrderByDayOfWeekLessonIndex(
            @Param("classGroup") String classGroup
    );

    @Query("""
            SELECT t
            FROM Timetable t
            ORDER BY t.dayOfTheWeek ASC, t.lessonIdx ASC
            """)
    List<Timetable> findAllByOrderByDayOfWeekLessonIndex();

    @Query("""
            SELECT COUNT(t)
            FROM Timetable t
            WHERE t.dayOfTheWeek = :dayOfWeek
            """)
    long countByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek);
}