package org.backend.qedu.repo;

import org.backend.qedu.entities.StudentSubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentSubjectEnrollmentRepo extends JpaRepository<StudentSubjectEnrollment, Long> {

    List<StudentSubjectEnrollment> findByClassSubject_Id(Long classSubjectId);

    long countByClassSubject_Id(Long classSubjectId);

    void deleteByClassSubject_Id(Long classSubjectId);

    @Modifying
    @Query("delete from StudentSubjectEnrollment e where e.student.ID = :sid")
    void deleteAllByStudentId(@Param("sid") Long studentUserId);

    boolean existsByClassSubject_IdAndStudent_ID(Long classSubjectId, Long studentId);

    @Query("select e.student.ID from StudentSubjectEnrollment e where e.classSubject.id = :csid")
    List<Long> findStudentIdsByClassSubjectId(@Param("csid") Long csid);

    @Query("select e.student.userName from StudentSubjectEnrollment e where e.classSubject.id = :csid")
    List<String> findStudentUsernamesByClassSubjectId(@Param("csid") Long csid);
}
