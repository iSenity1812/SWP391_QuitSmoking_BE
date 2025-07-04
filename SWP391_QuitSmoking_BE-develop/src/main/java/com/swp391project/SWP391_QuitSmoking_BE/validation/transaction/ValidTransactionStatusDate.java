package com.swp391project.SWP391_QuitSmoking_BE.validation.transaction;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Áp dụng ở cấp độ lớp (entity Transaction)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransactionStatusDateValidator.class) // Chỉ định lớp validator
@Documented
public @interface ValidTransactionStatusDate {
    String message() default "Ngày giao dịch phải được ghi nhận khi trạng thái là SUCCESS";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
