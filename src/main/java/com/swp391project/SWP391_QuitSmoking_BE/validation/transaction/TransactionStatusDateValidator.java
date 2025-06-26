package com.swp391project.SWP391_QuitSmoking_BE.validation.transaction;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TransactionStatusDateValidator implements ConstraintValidator<ValidTransactionStatusDate, Transaction> {
    @Override
    public void initialize(ValidTransactionStatusDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(Transaction transaction, ConstraintValidatorContext context) {
        if (transaction == null) {
            return true;
        }

        String status = transaction.getStatus();
        LocalDateTime updatedAt = transaction.getUpdatedAt();

        // Nếu trạng thái là SUCCESS, thì updatedAt không được null
        // Trong các trường hợp khác, updatedAt có thể null
        if ("SUCCESS".equals(status) && updatedAt == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày cập nhật không được để trống khi trạng thái là SUCCESS")
                    .addPropertyNode("updatedAt")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
