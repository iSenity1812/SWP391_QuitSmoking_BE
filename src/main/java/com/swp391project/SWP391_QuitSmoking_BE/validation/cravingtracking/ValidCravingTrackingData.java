package com.swp391project.SWP391_QuitSmoking_BE.validation.cravingtracking;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CravingTrackingDataValidator.class)
@Documented
public @interface ValidCravingTrackingData {
    String message() default "Dữ liệu theo dõi cơn thèm không hợp lệ.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
