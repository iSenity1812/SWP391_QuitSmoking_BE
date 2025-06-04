package com.swp391project.SWP391_QuitSmoking_BE.util;

import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuitPlanCalculator {
    @Getter
    @AllArgsConstructor
    public static class QuitPlanDay {
        private int day;
        private int cigarettes;
    }

    public List<QuitPlanDay> generateQuitPlan (
            int initialCigarettes,
            long totalDays, //Đổi sang long vì ChronoUnit.DAYS.between trả về long
            ReductionQuitPlanType reductionType
    ) {
        List<QuitPlanDay> plan = new ArrayList<>();

        //Validation đầu vào
        if (initialCigarettes < 0) {
            System.err.println("Lỗi: Số điếu ban đầu (S) phải là số nguyên không âm");
            return plan;
        }
        if (totalDays < 0) {
            System.err.println("Lỗi: Tổng số ngày (N) phải là số nguyên không âm");
            return plan;
        }

        //Xử lý các trường hợp biên của totalDays
        if (totalDays == 0) {
            return plan; //Kế hoạch không có ngày nào
        }
        if (totalDays == 1) {
            // Nếu kế hoạch chỉ có 1 ngày, mục tiêu là cai ngay lập tức trong ngày đó
            plan.add(new QuitPlanDay(1, 0));
            return plan;
        }

        //Logic tạo kế hoạch
        if (initialCigarettes >= totalDays) {
            // Trường hợp 1: Số điếu ban đầu lớn hơn hoặc bằng tổng số ngày
            // Cho phép giảm ít nhất 1 điếu mỗi ngày
            for (int i = 0; i < totalDays; i++) {
                double currentCigarettesValue = 0;
                // t: tỷ lệ thời gian từ 0 (ngày đầu tiên) đến 1 (ngày cuối cùng).
                // Sử dụng (totalDays - 1) để đảm bảo t đạt 1 chính xác vào ngày cuối cùng của chuỗi tính toán
                double t = (double) i / (totalDays - 1);

                switch (reductionType) {
                    case LINEAR: // Giảm tuyến tính - giảm đều mỗi ngày
                        currentCigarettesValue = initialCigarettes * (1 - t);
                        break;
                    case EXPONENTIAL: //Giảm theo hàm mũ - giảm nhanh hơn ban đầu, sau đó chậm lại
                        final double epsilon = 0.1;
                        double k = Math.log(initialCigarettes / epsilon) / (totalDays - 1);
                        currentCigarettesValue = initialCigarettes * Math.exp(-k * i);
                        break;
                    case LOGARITHMIC: //Giảm theo hàm logarit - giảm nhanh ở đầu, sau đó chậm lại
                        double ratio = Math.log10(1 + 9 * t);
                        currentCigarettesValue = initialCigarettes * (1 - ratio);
                        break;
                    default:
                        // Mặc định hoặc xử lý lỗi
                        currentCigarettesValue = initialCigarettes * (1 - t); //mặc định là giảm tuyến tính
                        break;
                }

                // Làm tròn số điếu thuốc về số nguyên gần nhất
                int roundedCigarettes = (int) Math.round(currentCigarettesValue);

                // Đảm bảo số điếu không bao giờ âm (do làm tròn hoặc sai số tính toán)
                roundedCigarettes = Math.max(0, roundedCigarettes);

                // Ép số điếu về 0 vào ngày cuối cùng để đảm bảo mục tiêu cai hoàn toàn
                if (i == totalDays - 1) {
                    roundedCigarettes = 0;
                }

                plan.add(new QuitPlanDay(i + 1, roundedCigarettes));
            }
        } else {
            // Trường hợp 2: Số điếu ban đầu nhỏ hơn tổng số ngày
            // Phù hợp hơn với chiến lược giảm theo từng "block" (giữ nguyên số điếu trong vài ngày rồi giảm)

            // blockSize: số ngày người dùng duy trì ở một mức điếu thuốc
            long blockSize = (initialCigarettes == 0) ? totalDays : (totalDays / initialCigarettes);
            int currentDay = 1;

            // Giảm số điếu thuốc từ initialCigarettes xuống 1
            for (int cigsPerDay = initialCigarettes; cigsPerDay > 0; cigsPerDay--) {
                // Gán mức điếu hiện tại cho 'blockSize'
                for (int j = 0; j < blockSize && currentDay <= totalDays; j++) {
                    plan.add(new QuitPlanDay(currentDay++, cigsPerDay));
                }
            }

            // Sau khi giảm đến 1 điếu, điền số ngày còn lại với 0 điếu
            while (currentDay <= totalDays) {
                plan.add(new QuitPlanDay(currentDay++, 0));
            }
        }
        return plan;
    }
}
