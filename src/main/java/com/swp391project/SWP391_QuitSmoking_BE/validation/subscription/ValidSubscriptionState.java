// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/validation/subscription/ValidSubscriptionState.java
package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SubscriptionStateValidator.class) // Đảm bảo bạn có SubscriptionStateValidator
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSubscriptionState {
    String message() default "Trạng thái gói đăng ký không nhất quán.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}