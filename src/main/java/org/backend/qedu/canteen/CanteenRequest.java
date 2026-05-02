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
    @NotBlank String audience;   //Students,Teachers, others

    @NotNull Integer amount;

    @NotBlank String soup;

    @NotBlank String secondMeal;

    @NotBlank String drinks;

    @NotBlank String desserts;


    /*public LocalDate getmenuDateTime(){
        return menuDateTime;
    }

    public String getMainMeal(){
        return mainMeal;
    }
    public String getDailyMenu(){
        return dailyMenu;
    }
    public String getWeeklyMenu(){
        return weeklyMenu;
    }
    public String getAudience(){
        return audience;
    }
    public Integer getAmount(){
        return amount;
    }
    public String getSoup(){
        return soup;
    }

    public String getSecondMeal(){
        return secondMeal;
    }
    public String getDrinks(){
        return drinks;
    }
    public String getDesserts(){
        return desserts;
    }*/
}
