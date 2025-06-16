// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/validation/subscription/SubscriptionStateValidator.java
package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus; // Import enum SubscriptionStatus
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class SubscriptionStateValidator implements ConstraintValidator<ValidSubscriptionState, Member> {

    @Override
    public void initialize(ValidSubscriptionState constraintAnnotation) {
    }

    @Override
    public boolean isValid(Member member, ConstraintValidatorContext context) {
        boolean isValid = true;
        context.disableDefaultConstraintViolation(); // Tắt tin nhắn mặc định

        if (member == null || member.getMemberSubscriptions() == null || member.getMemberSubscriptions().isEmpty()) {
            // Nếu không có gói đăng ký nào, coi như hợp lệ cho validator này.
            // Điều này có thể được xử lý bởi các validator khác nếu cần phải có gói.
            return true;
        }

        // Ưu tiên tìm gói đang ACTIVE. Nếu không có, lấy gói mới nhất (có thể EXPIRED/CANCELLED...)
        Optional<MemberSubscription> activeSubscriptionOpt = member.getMemberSubscriptions().stream()
                .filter(sub -> sub.getSubscriptionStatus() == SubscriptionStatus.ACTIVE)
                .max(Comparator.comparing(MemberSubscription::getStartDate)); // Lấy gói active gần nhất bắt đầu

        Optional<MemberSubscription> latestSubscriptionOpt = member.getMemberSubscriptions().stream()
                .max(Comparator.comparing(MemberSubscription::getPurchasedAt)); // Lấy gói được mua gần nhất

        MemberSubscription relevantSubscription = null;
        if (activeSubscriptionOpt.isPresent()) {
            relevantSubscription = activeSubscriptionOpt.get();
        } else if (latestSubscriptionOpt.isPresent()) {
            relevantSubscription = latestSubscriptionOpt.get();
        }

        if (relevantSubscription != null) {
            LocalDateTime now = LocalDateTime.now();

            // Kiểm tra tính nhất quán giữa trạng thái và ngày tháng
            if (relevantSubscription.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
                if (relevantSubscription.getStartDate() == null || relevantSubscription.getEndDate() == null) {
                    context.buildConstraintViolationWithTemplate("Gói đăng ký ACTIVE phải có ngày bắt đầu và kết thúc.")
                            .addPropertyNode("memberSubscriptions").addConstraintViolation();
                    isValid = false;
                } else if (now.isBefore(relevantSubscription.getStartDate()) || now.isAfter(relevantSubscription.getEndDate())) {
                    context.buildConstraintViolationWithTemplate("Gói đăng ký đang ACTIVE nhưng ngày tháng không hợp lệ (ngoài thời gian hiệu lực).")
                            .addPropertyNode("memberSubscriptions").addConstraintViolation();
                    isValid = false;
                }
            } else if (relevantSubscription.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
                if (relevantSubscription.getEndDate() == null) {
                    context.buildConstraintViolationWithTemplate("Gói đăng ký EXPIRED phải có ngày kết thúc.")
                            .addPropertyNode("memberSubscriptions").addConstraintViolation();
                    isValid = false;
                } else if (now.isBefore(relevantSubscription.getEndDate())) {
                    context.buildConstraintViolationWithTemplate("Gói đăng ký EXPIRED nhưng ngày kết thúc chưa đến.")
                            .addPropertyNode("memberSubscriptions").addConstraintViolation();
                    isValid = false;
                }
            } else if (relevantSubscription.getSubscriptionStatus() == SubscriptionStatus.PENDING) {
                if (relevantSubscription.getStartDate() == null || now.isAfter(relevantSubscription.getStartDate())) {
                    context.buildConstraintViolationWithTemplate("Gói đăng ký PENDING nhưng đã quá ngày bắt đầu hoặc không có ngày bắt đầu.")
                            .addPropertyNode("memberSubscriptions").addConstraintViolation();
                    isValid = false;
                }
            }
            // Có thể thêm các kiểm tra cho các trạng thái khác như CANCELLED, PAUSED, INACTIVE nếu cần
        }

        return isValid;
    }
}