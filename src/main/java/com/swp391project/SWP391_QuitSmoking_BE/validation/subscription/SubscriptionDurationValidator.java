//package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;
//
//import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//
//import java.time.LocalDateTime;
//
//public class SubscriptionDurationValidator implements ConstraintValidator<ValidSubscriptionDuration, MemberSubscription> {
//    private static final long MAX_DURATION_MONTHS = 3;
//
//    @Override
//    public void initialize(ValidSubscriptionDuration constraintAnnotation) {
//    }
//
//    @Override
//    public boolean isValid(MemberSubscription memberSubscription, ConstraintValidatorContext context) {
//        // Nếu đối tượng member, startDate hoặc endDate là null,
//        // các validation @NotNull khác sẽ xử lý. Validator này chỉ kiểm tra thời lượng nếu cả hai ngày đều có
//        if (memberSubscription == null || memberSubscription.getStartDate() == null || memberSubscription.getEndDate() == null) {
//            return true;
//        }
//
//        LocalDateTime startDate = memberSubscription.getStartDate();
//        LocalDateTime endDate = memberSubscription.getEndDate();
//
//        // Đảm bảo ngày kết thúc không trước ngày bắt đầu
//        if (endDate.isBefore(startDate)) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Ngày kết thúc không thể trước ngày bắt đầu")
//                    .addPropertyNode("endDate") // Gắn lỗi với trường endDate
//                    .addConstraintViolation();
//            return false;
//        }
//
//        // Tính toán ngày kết thúc tối đa cho phép (startDate + 3 tháng)
//        LocalDateTime maxEndDateAllowed = startDate.plusMonths(MAX_DURATION_MONTHS);
//
//        // Kiểm tra nếu endDate vượt quá maxEndDateAllowed
//        if (endDate.isAfter(maxEndDateAllowed)) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Ngày kết thúc đăng ký không được vượt quá " + MAX_DURATION_MONTHS + " tháng kể từ ngày bắt đầu.")
//                    .addPropertyNode("endDate") // Gắn lỗi với trường endDate
//                    .addConstraintViolation();
//            return false;
//        }
//
//        return true;
//    }
//}
