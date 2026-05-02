package org.backend.qedu.repo;
import org.backend.qedu.entities.SchoolEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
public interface EventRepo extends JpaRepository<SchoolEvents, Long>{
    List<SchoolEvents> findByAudienceInAndStartsAtAfterOrderByStartsAtAsc(
            List<String> audiences,
            LocalDateTime now
    );

    long countByStartsAtBetween(LocalDateTime from, LocalDateTime to);
}
