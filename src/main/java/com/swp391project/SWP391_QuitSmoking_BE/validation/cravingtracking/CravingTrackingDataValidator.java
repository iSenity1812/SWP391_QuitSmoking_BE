package com.swp391project.SWP391_QuitSmoking_BE.validation.cravingtracking;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;

public class CravingTrackingDataValidator implements ConstraintValidator<ValidCravingTrackingData, CravingTracking> {

    @Override
    public void initialize(ValidCravingTrackingData constraintAnnotation) {
    }

    @Override
    public boolean isValid(CravingTracking cravingTracking, ConstraintValidatorContext context) {
        if (cravingTracking == null) {
            return true;
        }

        //Nếu cả smokedCount và cravingsCount đều bằng 0, không hợp lệ
        boolean smokedCountIsZero = (cravingTracking.getSmokedCount() == 0);
        boolean cravingsCountIsZero = (cravingTracking.getCravingsCount() == 0);

        if (smokedCountIsZero && cravingsCountIsZero) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Không thể tạo bản ghi theo dõi cơn thèm nếu cả số lượng thuốc đã hút và số lần thèm thuốc đều bằng 0")
                    .addConstraintViolation(); // Báo Lỗi cấp lớp
            return false;
        }
        return true;
    }
}
