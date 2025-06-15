package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class SubscriptionStateValidator implements ConstraintValidator<ValidSubscriptionState, Member> {
    @Override
    public void initialize(ValidSubscriptionState constraintAnnotation) {
    }

    @Override
    public boolean isValid(Member member, ConstraintValidatorContext context) {
        boolean isValid = true;
        LocalDateTime currentTime = LocalDateTime.now();

        if (member == null) {
            return true;
        }

        context.disableDefaultConstraintViolation(); //Tắt tin nhắn mặc định để tạo tin nhắn tùy chỉnh

        //Logic khi trạng thái đăng ký là đã đăng ký (true)
        if (member.isSubscriptionStatus()) { // Nếu trạng thái là đã đăng ký (true) - Dòng này đã đúng
            // Các trường liên quan đến gói phải có giá trị
            if (member.getSubscription() == null || member.getSubscription().getSubscriptionId() == null) { // Đảm bảo getSubscription() không null
                context.buildConstraintViolationWithTemplate("Subscription ID không được để trống khi đã đăng ký")
                        .addPropertyNode("subscriptionId").addConstraintViolation();
                isValid = false;
            }
            if (member.getStartDate() == null) {
                context.buildConstraintViolationWithTemplate("Ngày bắt đầu không được để trống khi đã đăng ký")
                        .addPropertyNode("startDate").addConstraintViolation();
                isValid = false;
            }
            if (member.getEndDate() == null) {
                context.buildConstraintViolationWithTemplate("Ngày kết thúc không được để trống khi đã đăng ký")
                        .addPropertyNode("endDate").addConstraintViolation();
                isValid = false;
            } else { //Thời gian khi trạng thái là true
                if (member.getEndDate().isBefore(member.getStartDate())) {
                    context.buildConstraintViolationWithTemplate("Ngày kết thúc không được trước ngày bắt đầu")
                            .addPropertyNode("endDate").addConstraintViolation();
                    isValid = false;
                }
            }
            //status không được là TRUE nếu thời gian thực đã VƯỢT QUA enddate
            if (member.getEndDate() != null) {
                if (currentTime.isAfter(member.getEndDate())) {
                    context.buildConstraintViolationWithTemplate("Trạng thái đăng ký không được là 'đã đăng ký' khi gói đã hết hạn")
                            .addPropertyNode("subscriptionStatus").addConstraintViolation();
                    isValid = false;
                }
            }
        } else { // Nếu trạng thái là Chưa đăng ký (false)
            // Các trường liên quan đến gói phải là null
            if (member.getSubscription() != null && member.getSubscription().getSubscriptionId() != null) { // Đảm bảo getSubscription() không null
                context.buildConstraintViolationWithTemplate("Subscription ID phải là null khi chưa đăng ký")
                        .addPropertyNode("subscriptionId").addConstraintViolation();
                isValid = false;
            }
            if (member.getStartDate() != null) {
                context.buildConstraintViolationWithTemplate("Ngày bắt đầu phải là null khi chưa đăng ký")
                        .addPropertyNode("startDate").addConstraintViolation();
                isValid = false;
            }
            if (member.getEndDate() != null) {
                context.buildConstraintViolationWithTemplate("Ngày kết thúc phải là null khi chưa đăng ký")
                        .addPropertyNode("endDate").addConstraintViolation();
                isValid = false;
            }
            //kiểm tra thời gian thực khi status là FALSE
            //status không được là FALSE nếu thời gian thực đang nằm trong giai đoạn gói (startDate <= currentTime <= endDate)
            if (member.getStartDate() != null && member.getEndDate() != null) { // Đảm bảo cả hai ngày không null
                if (!currentTime.isBefore(member.getStartDate()) && !currentTime.isAfter(member.getEndDate())) {
                    context.buildConstraintViolationWithTemplate("Trạng thái đăng ký không được là 'chưa đăng ký' khi thời gian hiện tại nằm trong giai đoạn gói")
                            .addPropertyNode("subscriptionStatus").addConstraintViolation();
                    isValid = false;
                }
            }
        }
        return isValid;
    }
}