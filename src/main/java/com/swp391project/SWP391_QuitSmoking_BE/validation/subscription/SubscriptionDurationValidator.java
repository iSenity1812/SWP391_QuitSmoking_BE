// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/validation/subscription/SubscriptionDurationValidator.java
package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.ChronoUnit;
import java.util.Comparator; // Import này cần thiết
import java.util.Optional;

public class SubscriptionDurationValidator implements ConstraintValidator<ValidSubscriptionDuration, Member> {
    @Override
    public void initialize(ValidSubscriptionDuration constraintAnnotation) {
    }

    @Override
    public boolean isValid(Member member, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (member == null || member.getMemberSubscriptions() == null || member.getMemberSubscriptions().isEmpty()) {
            // Nếu không có gói đăng ký nào, coi như hợp lệ cho validator này.
            // Các validator @NotNull hoặc @Valid khác có thể xử lý việc thiếu gói nếu cần.
            return true;
        }

        context.disableDefaultConstraintViolation(); // Tắt tin nhắn mặc định

        // Tìm gói đăng ký MỚI NHẤT đã mua để kiểm tra thời lượng.
        // Bạn có thể thay đổi logic này để kiểm tra gói đang ACTIVE nếu muốn.
        Optional<MemberSubscription> latestSubscriptionOpt = member.getMemberSubscriptions().stream()
                .max(Comparator.comparing(MemberSubscription::getPurchasedAt)); // Lấy gói mới nhất theo thời gian mua

        if (latestSubscriptionOpt.isPresent()) {
            MemberSubscription latestSubscription = latestSubscriptionOpt.get();

            // Kiểm tra các trường ngày tháng của gói đăng ký mới nhất
            if (latestSubscription.getStartDate() == null || latestSubscription.getEndDate() == null) {
                context.buildConstraintViolationWithTemplate("Gói đăng ký phải có ngày bắt đầu và ngày kết thúc.")
                        .addPropertyNode("memberSubscriptions").addConstraintViolation();
                isValid = false;
            } else {
                long durationInDays = ChronoUnit.DAYS.between(latestSubscription.getStartDate(), latestSubscription.getEndDate());
                if (durationInDays < 0) {
                    context.buildConstraintViolationWithTemplate("Ngày kết thúc gói đăng ký không được trước ngày bắt đầu.")
                            .addPropertyNode("memberSubscriptions").addConstraintViolation();
                    isValid = false;
                }
                // Thêm logic kiểm tra duration cụ thể nếu có (ví dụ: gói 30 ngày, 90 ngày)
                // if (latestSubscription.getSubscription() != null && latestSubscription.getSubscription().getDuration() != durationInDays) {
                //     context.buildConstraintViolationWithTemplate("Thời lượng gói đăng ký không khớp với loại gói đã chọn.")
                //             .addPropertyNode("memberSubscriptions").addConstraintViolation();
                //     isValid = false;
                // }
            }
        } else {
            // Không có gói nào để kiểm tra, coi như hợp lệ cho validator này nếu không có yêu cầu đặc biệt.
            // Điều này có thể xảy ra nếu danh sách MemberSubscriptions rỗng sau khi filter.
        }

        return isValid;
    }
}