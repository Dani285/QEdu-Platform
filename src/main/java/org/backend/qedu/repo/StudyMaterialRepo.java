package org.backend.qedu.repo;

import org.backend.qedu.entities.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyMaterialRepo extends JpaRepository<StudyMaterial, Long> {

    List<StudyMaterial> findByTeacherUsernameOrderByUpdatedAtDesc(String teacherUsername);

    List<StudyMaterial> findByClassIdOrderByUpdatedAtDesc(String classId);

    List<StudyMaterial> findAllByOrderByUpdatedAtDesc();
}
