//package com.swp391project.SWP391_QuitSmoking_BE.validation.subscription;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.*;
//
//@Target({ElementType.TYPE}) //áp dụng ở cấp độ lớp -> tương tác được với cả field startDate -> tính toán
//@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = SubscriptionDurationValidator.class) // Chỉ định lớp validator
//@Documented
////@interface: là annotation (chú thích) tùy chỉnh của riêng mình
//public @interface ValidSubscriptionDuration {
//    String message() default "Ngày kết thúc đăng ký không được vượt quá 3 tháng kể từ ngày bắt đầu";
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
//}
