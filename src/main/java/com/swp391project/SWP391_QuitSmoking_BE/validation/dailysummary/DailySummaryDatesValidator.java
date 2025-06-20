package com.swp391project.SWP391_QuitSmoking_BE.validation.dailysummary;

import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.QuitPlanCalculator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class DailySummaryDatesValidator implements ConstraintValidator<ValidDailySummaryDates, DailySummary> {
    @Autowired //để kiểm tra tính duy nhất
    private DailySummaryRepository dailySummaryRepository;

    @Autowired
    private QuitPlanCalculator quitPlanCalculator;

    @Override
    public void initialize(ValidDailySummaryDates constraintAnnotation) {
        //Không cần khởi tạo gì ở đây
    }

    @Override
    public boolean isValid(DailySummary dailySummary, ConstraintValidatorContext context) {
        if (dailySummary == null || dailySummary.getQuitPlan() == null || dailySummary.getTrackDate() == null) {
            //Để các validator @NotNull khác xử lý các trường null
            return true;
        }

        QuitPlan quitPlan = dailySummary.getQuitPlan();
        LocalDate trackDate = dailySummary.getTrackDate();

        LocalDateTime quitPlanStartDate = quitPlan.getStartDate();
        LocalDate quitPlanGoalDate = quitPlan.getGoalDate();
        int initialSmokingAmount = quitPlan.getInitialSmokingAmount();

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        //TrackDate phải nằm giữa QuitPlan.StartDate và QuitPlan.GoalDate.
        if (quitPlanStartDate != null && quitPlanGoalDate != null) {
            LocalDate planStartDateAsDate = quitPlanStartDate.toLocalDate();
            if (trackDate.isBefore(planStartDateAsDate) || trackDate.isAfter(quitPlanGoalDate)) {
                context.buildConstraintViolationWithTemplate("Ngày theo dõi phải nằm trong khoảng thời gian của kế hoạch cai thuốc (từ ngày bắt đầu đến ngày mục tiêu)")
                        .addPropertyNode("trackDate")
                        .addConstraintViolation();
                isValid = false;
            }
        } else {
            //Nếu không có ngày bắt đầu hoặc ngày mục tiêu, không thể validate
            context.buildConstraintViolationWithTemplate("Thông tin kế hoạch (StartDate, GoalDate) không đầy đủ để kiểm tra ngày theo dõi")
                    .addPropertyNode("trackDate")
                    .addConstraintViolation();
            isValid = false;
        }

        //TrackDate là duy nhất cho mỗi QuitPlan
        Optional<DailySummary> existingSummary = dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate);
        if (existingSummary.isPresent() && (dailySummary.getDailySummaryId() == null || !existingSummary.get().getDailySummaryId().equals(dailySummary.getDailySummaryId()))) {
            context.buildConstraintViolationWithTemplate("Đã có nhật ký cho ngày này trong kế hoạch cai thuốc hiện tại")
                    .addPropertyNode("trackDate")
                    .addConstraintViolation();
            isValid = false;
        }

        //điều kiện để goal trong ngày đó chỉ được true khi smokedCount <= số điếu nên hút
        if (dailySummary.isGoalAchievedToday()) { //Nếu đánh dấu là kế hoạch đã hoàn thành trong ngày
            if (quitPlanStartDate != null && quitPlanGoalDate != null) {
                //ChronoUnit.DAYS.between: tính số ngày giữa hai LocalDate + 1 để bao gồm cả ngày bắt đầu
                long totalDays = ChronoUnit.DAYS.between(quitPlanStartDate.toLocalDate(), quitPlanGoalDate) + 1;

                ReductionQuitPlanType reductionType = quitPlan.getReductionType();

                if (reductionType != null) {
                    List<QuitPlanCalculator.QuitPlanDay> plan = quitPlanCalculator.generateQuitPlan(
                            initialSmokingAmount,
                            totalDays,
                            reductionType
                    );

                    //Tìm số điếu thuốc cho phép vào ngày trackDate
                    // Ngày thứ mấy trong kế hoạch (bắt đầu từ 1)
                    long dayIndex = ChronoUnit.DAYS.between(quitPlanStartDate.toLocalDate(), trackDate) + 1;

                    Optional<QuitPlanCalculator.QuitPlanDay> allowedDay = plan.stream()
                            .filter(d -> d.getDay() == dayIndex)
                            .findFirst();

                    if (allowedDay.isPresent()) {
                        int allowedCigarettes = allowedDay.get().getCigarettes();
                        if (dailySummary.getTotalSmokedCount() > allowedCigarettes) {
                            context.buildConstraintViolationWithTemplate("Số điếu thuốc đã hút (" + dailySummary.getTotalSmokedCount() + ") vượt quá số điếu cho phép (" + allowedCigarettes + ") để đánh dấu 'Hoàn thành kế hoạch'")
                                    .addPropertyNode("smokedCount")
                                    .addConstraintViolation();
                            isValid = false;
                        }
                    } else {
                        // Trường hợp không tìm thấy ngày trong kế hoạch (lỗi logic hoặc ngày ngoài phạm vi)
                        context.buildConstraintViolationWithTemplate("Không thể xác định số điếu thuốc cho phép cho ngày này trong kế hoạch")
                                .addPropertyNode("trackDate")
                                .addConstraintViolation();
                        isValid = false;
                    }
                }
            } else {
                //Nếu các trường cần thiết để tính toán kế hoạch là null, không thể validate
                context.buildConstraintViolationWithTemplate("Thông tin kế hoạch (StartDate, GoalDate, PlanType) không đầy đủ để kiểm tra hoàn thành kế hoạch")
                        .addPropertyNode("isPlanCompleted")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
