package com.swp391project.SWP391_QuitSmoking_BE.validation.quitplan;

import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuitPlanDatesValidator implements ConstraintValidator<ValidQuitPlanDates, QuitPlan> {

    private static final long TIMING_TOLERANCE_SECONDS = 5;

    @Override
    public void initialize(ValidQuitPlanDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(QuitPlan quitPlan, ConstraintValidatorContext context) {
        if (quitPlan == null) {
            return true; //Để các validator @NotNull khác xử lý đối tượng null
        }

        LocalDateTime createdAt = quitPlan.getCreatedAt();
        LocalDateTime startDate = quitPlan.getStartDate();
        LocalDate goalDate = quitPlan.getGoalDate();
        QuitPlanStatus status = quitPlan.getStatus();
        LocalDateTime currentTime = LocalDateTime.now();

        boolean isValid = true;
        context.disableDefaultConstraintViolation(); //Tắt tin nhắn mặc định để tạo tin nhắn tùy chỉnh

//        // CreatedAt không được ở tương lai (có tolerance)
//        if (createdAt != null && createdAt.isAfter(currentTime.plusSeconds(TIMING_TOLERANCE_SECONDS))) {
//            context.buildConstraintViolationWithTemplate("Ngày tạo kế hoạch không thể ở tương lai")
//                    .addPropertyNode("createdAt")
//                    .addConstraintViolation();
//            isValid = false;
//        }

        //CreatedAt không được sau StartDate
        if (createdAt != null && startDate != null) {
            if (createdAt.isAfter(startDate)) {
                context.buildConstraintViolationWithTemplate("Ngày tạo kế hoạch không được sau ngày bắt đầu")
                        .addPropertyNode("createdAt")
                        .addConstraintViolation();
                isValid = false;
            }
        }


        //StartDate không được sau GoalDate
        if (startDate != null && goalDate != null) {
            if (startDate.toLocalDate().isAfter(goalDate)) {
                context.buildConstraintViolationWithTemplate("Ngày bắt đầu không được sau ngày mục tiêu")
                        .addPropertyNode("startDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        LocalDateTime tolerantCurrentTime = currentTime.minusSeconds(TIMING_TOLERANCE_SECONDS);
        LocalDateTime futureToleranceTime = currentTime.plusSeconds(TIMING_TOLERANCE_SECONDS);

        //Nếu goalDate chưa bằng hay vượt qua thời gian thực thì status không thể là FAILED hay COMPLETED
        if (goalDate != null && currentTime.toLocalDate().isBefore(goalDate)) {
            if (status == QuitPlanStatus.FAILED || status == QuitPlanStatus.COMPLETED) {
                context.buildConstraintViolationWithTemplate("Kế hoạch không thể ở trạng thái 'Hoàn thành', 'Thất bại' khi ngày mục tiêu còn ở tương lai")
                        .addPropertyNode("status")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        //Nếu thời gian thực đã vượt qua goalDate thì status phải là FAILED, COMPLETED
        if (goalDate != null && currentTime.toLocalDate().isAfter(goalDate)) {
            if (status != QuitPlanStatus.FAILED && status != QuitPlanStatus.COMPLETED) {
                context.buildConstraintViolationWithTemplate("Kế hoạch phải ở trạng thái 'Hoàn thành', 'Thất bại' khi ngày mục tiêu đã qua")
                        .addPropertyNode("status")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        //Nếu thời gian thực chưa bằng hay vượt qua startdate thì status phải là NOT_STARTED
        if (startDate != null && futureToleranceTime.isBefore(startDate)) {
            if (status == QuitPlanStatus.COMPLETED || status == QuitPlanStatus.IN_PROGRESS ||
                    status == QuitPlanStatus.FAILED) {
                context.buildConstraintViolationWithTemplate("Kế hoạch không thể ở trạng thái 'Hoàn thành', 'Đang tiến hành', 'Thất bại', hoặc 'Bị bỏ dở' khi ngày bắt đầu còn ở tương lai")
                        .addPropertyNode("status")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        //Nếu thời gian thực đã bằng hay vượt qua startdate thì status không thể NOT_STARTED
        if (startDate != null && startDate.isAfter(futureToleranceTime)) {
            if (status != QuitPlanStatus.NOT_STARTED) {
                context.buildConstraintViolationWithTemplate("Kế hoạch phải ở trạng thái 'Chưa bắt đầu' khi ngày bắt đầu còn ở tương lai")
                        .addPropertyNode("status")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Nếu startDate đã qua (trước tolerance), status không thể là NOT_STARTED
        if (startDate != null && startDate.isBefore(tolerantCurrentTime)) {
            if (status == QuitPlanStatus.NOT_STARTED) {
                context.buildConstraintViolationWithTemplate("Kế hoạch không thể ở trạng thái 'Chưa bắt đầu' khi ngày bắt đầu đã qua")
                        .addPropertyNode("status")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        //Nếu Status là IN_PROGRESS, thì GoalDate phải ở hiện tại hoặc tương lai, và StartDate phải ở quá khứ hoặc hiện tại
        if (status == QuitPlanStatus.IN_PROGRESS) {
            if (goalDate != null) {
                if (currentTime.toLocalDate().isAfter(goalDate)) { //currentTime > goalDate
                    context.buildConstraintViolationWithTemplate("Ngày mục tiêu phải ở hiện tại hoặc tương lai khi kế hoạch đang 'Đang tiến hành'")
                            .addPropertyNode("goalDate")
                            .addConstraintViolation();
                    isValid = false;
                }
            } else { //goalDate is null, but required for IN_PROGRESS
                context.buildConstraintViolationWithTemplate("Ngày mục tiêu không được để trống khi kế hoạch đang 'Đang tiến hành'")
                        .addPropertyNode("goalDate")
                        .addConstraintViolation();
                isValid = false;
            }

            if (startDate != null) {
                if (futureToleranceTime.isBefore(startDate)) { //currentTime < startDate
                    context.buildConstraintViolationWithTemplate("Ngày bắt đầu phải ở quá khứ hoặc hiện tại khi kế hoạch đang 'Đang tiến hành'")
                            .addPropertyNode("startDate")
                            .addConstraintViolation();
                    isValid = false;
                }
            } else { //startDate is null, but required for IN_PROGRESS
                context.buildConstraintViolationWithTemplate("Ngày bắt đầu không được để trống khi kế hoạch đang 'Đang tiến hành'")
                        .addPropertyNode("startDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
