// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/validation/subscription/ValidSubscriptionDuration.java
package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SubscriptionDurationValidator.class) // Đảm bảo bạn có SubscriptionDurationValidator
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSubscriptionDuration {
    String message() default "Thời lượng gói đăng ký không hợp lệ.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}