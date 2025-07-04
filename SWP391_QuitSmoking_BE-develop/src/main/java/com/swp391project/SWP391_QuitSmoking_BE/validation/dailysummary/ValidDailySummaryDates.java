package com.swp391project.SWP391_QuitSmoking_BE.validation.dailysummary;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Áp dụng ở cấp độ lớp (entity DailySummary)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DailySummaryDatesValidator.class) // Chỉ định lớp validator
@Documented
public @interface ValidDailySummaryDates {
    String message() default "Ngày theo dõi không hợp lệ hoặc đã tồn tại cho kế hoạch này.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
