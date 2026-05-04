package org.backend.qedu.repo;
import org.backend.qedu.entities.GradeRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepo extends JpaRepository<GradeRecords, Long>{
    long countByStudentUsername(String studentUsername);

    @Query("select avg(g.grade) from GradeRecords g where g.studentUsername = :username")
    Double averageForStudent(String username);

    @Query("select avg(g.grade) from GradeRecords g where g.teacherUsername = :username")
    Double averageForTeacher(String username);

    List<GradeRecords> findByStudentUsernameOrderByCreatedTimeDesc(String studentUsername);

    List<GradeRecords> findByTeacherUsernameOrderByCreatedTimeDesc(String teacherUsername);

    List<GradeRecords> findAllByOrderByCreatedTimeDesc();

    @Query("select g from GradeRecords g where g.classGroup = :cg and lower(g.subjectName) = lower(:sn) order by g.createdTime desc")
    List<GradeRecords> findForClassGroupAndSubject(
            @Param("cg") String classGroup,
            @Param("sn") String subjectName
    );
    @Query("select avg(g.grade) from GradeRecords g")
    Double averageForSchool();
}
