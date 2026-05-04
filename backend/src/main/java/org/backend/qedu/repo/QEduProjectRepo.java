package org.backend.qedu.repo;

import org.backend.qedu.entities.QEduProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QEduProjectRepo extends JpaRepository<QEduProject, Long> {

    List<QEduProject> findByTeacherUsernameOrderByUpdatedAtDesc(String teacherUsername);

    List<QEduProject> findByClassIdOrderByUpdatedAtDesc(String classId);

    List<QEduProject> findAllByOrderByUpdatedAtDesc();
}
