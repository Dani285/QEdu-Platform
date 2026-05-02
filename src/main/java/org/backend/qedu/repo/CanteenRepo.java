package org.backend.qedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.backend.qedu.canteen.Canteen;

import java.util.List;
import java.time.LocalDate;
public interface CanteenRepo extends JpaRepository<Canteen, Long> {

    List<Canteen> findAllByOrderMenuAndDate();
    boolean existsMenuDate(LocalDate menuDateTime);
}
