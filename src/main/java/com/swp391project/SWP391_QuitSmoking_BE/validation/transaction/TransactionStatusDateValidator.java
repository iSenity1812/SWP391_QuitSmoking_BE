package com.swp391project.SWP391_QuitSmoking_BE.validation.transaction;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
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

        TransactionStatus status = transaction.getStatus();
        LocalDateTime transactionDate = transaction.getTransactionDate();

        //Nếu trạng thái là SUCCESS, thì transactionDate không được null
        //Trong các trường hợp khác, transactionDate có thể null
        if (status == TransactionStatus.SUCCESS && transactionDate == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày giao dịch không được để trống khi trạng thái là SUCCESS")
                    .addPropertyNode("transactionDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
