package org.backend.qedu.repo;

import org.backend.qedu.entities.ClassSubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassSubjectRepo extends JpaRepository<ClassSubjectAssignment, Long> {

    List<ClassSubjectAssignment> findAllByOrderByClassIdAscSubjectNameAsc();

    List<ClassSubjectAssignment> findByClassIdOrderBySubjectNameAsc(String classId);

    Optional<ClassSubjectAssignment> findByClassIdAndSubjectId(String classId, String subjectId);

    List<ClassSubjectAssignment> findByTeacherUsernameOrderByClassIdAsc(String teacherUsername);

    @Query("SELECT a FROM ClassSubjectAssignment a WHERE a.teacherUsername = :tu AND a.className = :cn AND LOWER(a.subjectName) = LOWER(:sn)")
    Optional<ClassSubjectAssignment> findForTeacherClassAndSubject(
            @Param("tu") String teacherUsername,
            @Param("cn") String className,
            @Param("sn") String subjectName
    );
}
