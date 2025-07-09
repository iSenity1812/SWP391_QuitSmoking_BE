package com.swp391project.SWP391_QuitSmoking_BE.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationTypeConstraintValidator.class)
@Documented
public @interface ValidDurationTypeConstraint {
    String message() default "Thời lượng không hợp lệ dựa trên dạng thời lượng";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
