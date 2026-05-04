package org.backend.qedu.repo;

import org.backend.qedu.entities.QEduExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QEduExamRepo extends JpaRepository<QEduExam, Long> {

    List<QEduExam> findByTeacherUsernameOrderByExamDateAsc(String teacherUsername);

    List<QEduExam> findByClassIdOrderByExamDateAsc(String classId);

    List<QEduExam> findAllByOrderByExamDateAsc();
}
