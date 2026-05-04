package org.backend.qedu.repo;

import org.backend.qedu.canteen.Canteen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface CanteenRepo extends JpaRepository<Canteen, Long> {

    List<Canteen> findAllByOrderByIDDesc();

    boolean existsByDailyMenuAndWeeklyMenu(String dailyMenu, String weeklyMenu);
}
