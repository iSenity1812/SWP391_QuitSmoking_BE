package com.swp391project.SWP391_QuitSmoking_BE.validation.quitplan;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuitPlanDatesValidator.class)
@Documented
public @interface ValidQuitPlanDates {
    String message() default "Các ngày trong kế hoạch cai thuốc không hợp lệ.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
