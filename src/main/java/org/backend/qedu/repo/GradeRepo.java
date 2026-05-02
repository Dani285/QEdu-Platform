package org.backend.qedu.repo;
import org.backend.qedu.entities.GradeRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface GradeRepo extends JpaRepository<GradeRecords, Long>{
    long countByStudentUsername(String studentUsername);

    @Query("select avg(g.grade) from GradeRecords g where g.studentUsername = :username")
    Double averageForStudent(String username);

    @Query("select avg(g.grade) from GradeRecords g where g.teacherUsername = :username")
    Double averageForTeacher(String username);

    @Query("select avg(g.grade) from GradeRecords g")
    Double averageForSchool();
}
