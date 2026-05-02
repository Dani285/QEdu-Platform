package org.backend.qedu.repo;

import org.backend.qedu.entities.AttendanceRecords;
import org.backend.qedu.model.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AttendanceRepo extends JpaRepository<AttendanceRecords, Long>{

    List<AttendanceRecords> findByStudentUsernameOrderByAttendanceDateDesc(String studentUsername);

    List<AttendanceRecords> findByTeacherUsernameOrderByAttendanceDateDesc(String teacherUsername);

    List<AttendanceRecords> findAllByOrderByAttendanceDateDesc();

    long countByStudentUsernameAndStatus(String studentUsername, AttendanceStatus.Status status);

    long countByTeacherUsernameAndStatus(String teacherUsername, AttendanceStatus.Status status);

    long countByStatus(AttendanceStatus.Status status);
}
