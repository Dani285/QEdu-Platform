package org.backend.qedu.canteen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CanteenRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @NotNull LocalDate menuDateTime;

    @NotBlank String mainMeal;
    @NotBlank String dailyMenu;

    @NotBlank String weeklyMenu;
    @NotBlank String audience;

    @NotNull Integer amount;

    @NotBlank String soup;

    @NotBlank String secondMeal;

    @NotBlank String drinks;

    @NotBlank String desserts;

}
