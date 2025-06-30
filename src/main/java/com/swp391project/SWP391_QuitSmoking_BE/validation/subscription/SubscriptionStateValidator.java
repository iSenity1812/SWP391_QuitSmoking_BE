//package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;
//
//import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
//import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//
//import java.time.LocalDateTime;
//
//public class SubscriptionStateValidator implements ConstraintValidator<ValidSubscriptionState, MemberSubscription> {
//    @Override
//    public void initialize(ValidSubscriptionState constraintAnnotation) {
//    }
//
//    @Override
//    public boolean isValid(MemberSubscription subscription, ConstraintValidatorContext context) {
//        boolean isValid = true;
//        LocalDateTime currentTime = LocalDateTime.now();
//
//        if (subscription == null) {
//            return true;
//        }
//
//        context.disableDefaultConstraintViolation(); //Tắt tin nhắn mặc định để tạo tin nhắn tùy chỉnh
//
//        if (subscription.getStartDate() != null && subscription.getEndDate() != null) {
//            if (subscription.getStartDate().isAfter(subscription.getEndDate())) {
//                context.buildConstraintViolationWithTemplate("Ngày bắt đầu không được sau ngày kết thúc")
//                        .addPropertyNode("startDate").addConstraintViolation();
//                isValid = false;
//            }
//        }
//        //Logic khi trạng thái đăng ký là đã đăng ký (true)
////        if (member.isSubscriptionStatus()) { // Nếu trạng thái là đã đăng ký (true)
////            // Các trường liên quan đến gói phải có giá trị
////            if (member.getSubscription().getSubscriptionId() == null) {
////                context.buildConstraintViolationWithTemplate("Subscription ID không được để trống khi đã đăng ký")
////                        .addPropertyNode("subscriptionId").addConstraintViolation();
////                isValid = false;
////            }
////            if (member.getStartDate() == null) {
////                context.buildConstraintViolationWithTemplate("Ngày bắt đầu không được để trống khi đã đăng ký")
////                        .addPropertyNode("startDate").addConstraintViolation();
////                isValid = false;
////            }
////            if (member.getEndDate() == null) {
////                context.buildConstraintViolationWithTemplate("Ngày kết thúc không được để trống khi đã đăng ký")
////                        .addPropertyNode("endDate").addConstraintViolation();
////                isValid = false;
////            } else { //Thời gian khi trạng thái là true
////                if (member.getEndDate().isBefore(member.getStartDate())) {
////                    context.buildConstraintViolationWithTemplate("Ngày kết thúc không được trước ngày bắt đầu")
////                            .addPropertyNode("endDate").addConstraintViolation();
////                    isValid = false;
////                }
////            }
////            //status không được là TRUE nếu thời gian thực đã VƯỢT QUA enddate
////            if (member.getEndDate() != null) {
////                if (currentTime.isAfter(member.getEndDate())) {
////                    context.buildConstraintViolationWithTemplate("Trạng thái đăng ký không được là 'đã đăng ký' khi gói đã hết hạn")
////                            .addPropertyNode("subscriptionStatus").addConstraintViolation();
////                    isValid = false;
////                }
////            }
////        } else { // Nếu trạng thái là Chưa đăng ký (false)
////            // Các trường liên quan đến gói phải là null
////            if (member.getSubscription().getSubscriptionId() != null) {
////                context.buildConstraintViolationWithTemplate("Subscription ID phải là null khi chưa đăng ký")
////                        .addPropertyNode("subscriptionId").addConstraintViolation();
////                isValid = false;
////            }
////            if (member.getStartDate() != null) {
////                context.buildConstraintViolationWithTemplate("Ngày bắt đầu phải là null khi chưa đăng ký")
////                        .addPropertyNode("startDate").addConstraintViolation();
////                isValid = false;
////            }
////            if (member.getEndDate() != null) {
////                context.buildConstraintViolationWithTemplate("Ngày kết thúc phải là null khi chưa đăng ký")
////                        .addPropertyNode("endDate").addConstraintViolation();
////                isValid = false;
////            }
////            //kiểm tra thời gian thực khi status là FALSE
////            //status không được là FALSE nếu thời gian thực đang nằm trong giai đoạn gói (startDate <= currentTime <= endDate)
////            if (member.getStartDate() != null && member.getEndDate() != null) { // Đảm bảo cả hai ngày không null
////                if (!currentTime.isBefore(member.getStartDate()) && !currentTime.isAfter(member.getEndDate())) {
////                    context.buildConstraintViolationWithTemplate("Trạng thái đăng ký không được là 'chưa đăng ký' khi thời gian hiện tại nằm trong giai đoạn gói")
////                            .addPropertyNode("subscriptionStatus").addConstraintViolation();
////                    isValid = false;
////                }
////            }
////        }
//
//        // Logic kiểm tra dựa trên trạng thái của gói đăng ký
//        if (subscription.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
//            // Khi gói đang ACTIVE:
//            // 1. Phải có StartDate và EndDate
//            if (subscription.getStartDate() == null || subscription.getEndDate() == null) {
//                context.buildConstraintViolationWithTemplate("Gói đang ACTIVE phải có ngày bắt đầu và ngày kết thúc")
//                        .addPropertyNode("status").addConstraintViolation();
//                isValid = false;
//            } else {
//                // 2. Thời gian hiện tại phải nằm trong khoảng StartDate và EndDate
//                // Nếu StartDate là tương lai, gói có thể ở PENDING/NOT_STARTED chứ không phải ACTIVE
//                if (currentTime.isBefore(subscription.getStartDate())) {
//                    context.buildConstraintViolationWithTemplate("Gói không thể ACTIVE khi ngày bắt đầu còn ở tương lai")
//                            .addPropertyNode("status").addConstraintViolation();
//                    isValid = false;
//                }
//                // Nếu thời gian hiện tại đã qua EndDate, gói phải là EXPIRED chứ không phải ACTIVE
//                if (currentTime.isAfter(subscription.getEndDate())) {
//                    context.buildConstraintViolationWithTemplate("Gói không thể ACTIVE khi ngày kết thúc đã qua")
//                            .addPropertyNode("status").addConstraintViolation();
//                    isValid = false;
//                }
//            }
//            // 3. SubscriptionType phải tồn tại (đã được kiểm tra qua @NotNull trên SubscriptionType)
//        } else if (subscription.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
//            // Khi gói EXPIRED:
//            // 1. EndDate phải đã qua
//            if (subscription.getEndDate() == null || currentTime.isBefore(subscription.getEndDate()) || currentTime.isEqual(subscription.getEndDate())) {
//                context.buildConstraintViolationWithTemplate("Gói phải là EXPIRED khi ngày kết thúc đã qua")
//                        .addPropertyNode("status").addConstraintViolation();
//                isValid = false;
//            }
//        }
//        return isValid;
//    }
//}
