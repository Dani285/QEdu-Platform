package org.backend.qedu.controllers;
import org.backend.qedu.canteen.Canteen;
import org.backend.qedu.canteen.CanteenRequest;
import org.backend.qedu.dto.AttendanceRequest;
import org.backend.qedu.dto.EventRequest;
import org.backend.qedu.dto.StatisticsResponse;
import org.backend.qedu.dto.TimeTableRequest;
import org.backend.qedu.entities.AttendanceRecords;
import org.backend.qedu.entities.SchoolEvents;
import org.backend.qedu.entities.Timetable;
import org.backend.qedu.model.AttendanceStatus;
import org.backend.qedu.service.QEduService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QEduController {

        private final QEduService qEduService;

        @GetMapping("/api/timetable")
        public List<Timetable> timetable(Authentication authentication) {
            return qEduService.getTimetable(qEduService.currentUser(authentication));
        }

        @PostMapping("/api/timetable")
        @PreAuthorize("hasRole('ADMIN')")
        public Timetable createTimetable(@Valid @RequestBody TimeTableRequest request) {
            return qEduService.createTimetable(request);
        }

        @GetMapping("/api/events")
        public List<SchoolEvents> events(Authentication authentication) {
            return qEduService.getEvents(qEduService.currentUser(authentication));
        }

        @PostMapping("/api/events")
        @PreAuthorize("hasRole('ADMIN')")
        public SchoolEvents createEvent(
                @Valid @RequestBody EventRequest request,
                Authentication authentication
        ) {
            return qEduService.createEvent(request, qEduService.currentUser(authentication));
        }

        @DeleteMapping("/api/events/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public void deleteEvent(@PathVariable Long id) {
            qEduService.deleteEvent(id);
        }

        @GetMapping("/api/attendance")
        public List<AttendanceRecords> attendance(Authentication authentication) {
            return qEduService.getAttendance(qEduService.currentUser(authentication));
        }

        @PostMapping("/api/attendance")
        @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
        public AttendanceRecords createAttendance(
                @Valid @RequestBody AttendanceRequest request,
                Authentication authentication
        ) {
            return qEduService.createAttendance(request, qEduService.currentUser(authentication));
        }

        @PutMapping("/api/attendance/{id}")
        @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
        public AttendanceRecords updateAttendance(
                @PathVariable Long id,
                @RequestParam AttendanceStatus.Status status
        ) {
            return qEduService.updateAttendance(id, status);
        }

        @GetMapping("/api/statistics")
        public StatisticsResponse statistics(Authentication authentication) {
            return qEduService.getStatistics(qEduService.currentUser(authentication));
        }

        @GetMapping("/api/canteen")
        public List<Canteen> canteenMenus(){
            return qEduService.getCanteenList();
        }
        @PostMapping("/api/canteen")
        @PreAuthorize("hasRole('CHEF')")
        public Canteen createCanteenMenu(@Valid @RequestBody CanteenRequest request, Authentication authentication){
            return qEduService.createCanteenMenu(request,qEduService.currentUser(authentication));
        }
        @DeleteMapping("api/canteen/{id}")
        @PreAuthorize("hasRole('CHEF')")
        public void DeleteCanteenMenu(@PathVariable Long id, Authentication authentication){
            qEduService.DeleteCanteenMenu(id,qEduService.currentUser(authentication));
        }
}
