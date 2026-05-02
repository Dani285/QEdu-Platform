package org.backend.qedu.repo;
import org.backend.qedu.entities.SchoolEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
public interface EventRepo extends JpaRepository<SchoolEvents, Long>{

    @Query("""
            SELECT e
            FROM SchoolEvents e
            WHERE e.Audience IN :audiences
              AND e.eventStartTime > :now
            ORDER BY e.eventStartTime ASC
            """)
    List<SchoolEvents> findByAudienceInAndStartsAtAfterOrderByStartsAtAsc(
            @Param("audiences") List<String> audiences,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT COUNT(e)
            FROM SchoolEvents e
            WHERE e.eventStartTime BETWEEN :from AND :to
            """)
    long countByStartsAtBetween(@Param("from")LocalDateTime from,
                                @Param("to")LocalDateTime to);
}
