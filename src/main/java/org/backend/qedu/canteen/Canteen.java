package org.backend.qedu.canteen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "canteen_menus")
@Getter
@Setter
public class Canteen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private String mainMeal;

    @Column(nullable = false)
    private String soup;

    private String secondMeal;

    private String drinks;

    private String deserts;

    @Column(nullable = false)
    private String audience;
    @Column(nullable = false)
    private Integer Amount;
    @Column(nullable = false)
    private String dailyMenu;
    @Column(nullable = false)
    private String weeklyMenu;

    @Column(nullable = false)
    private String ChefName;

    @Column(nullable = false)
    private String createdByChef;
}
