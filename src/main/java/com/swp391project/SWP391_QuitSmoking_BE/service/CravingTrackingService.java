package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

//có thể là một ngoại lệ tùy chỉnh cho lỗi nghiệp vụ này
class CravingTrackingEditForbiddenException extends RuntimeException {
    public CravingTrackingEditForbiddenException(String message) {
        super(message);
    }
}

class CravingTrackingDeletedException extends RuntimeException {
    public CravingTrackingDeletedException(String message) {
        super(message);
    }
}

@AllArgsConstructor
@Service
public class CravingTrackingService {
    private final CravingTrackingRepository cravingTrackingRepository;

    @Transactional
    public CravingTracking createCravingTracking(CravingTracking cravingTracking) {
        // Các Bean Validation trên entity sẽ tự động được kích hoạt
        return cravingTrackingRepository.save(cravingTracking);
    }

    @Transactional
    public CravingTracking updateCravingTracking(Integer cravingTrackingId, CravingTracking updatedCravingTracking) {
        Optional<CravingTracking> existingTrackingOptional = cravingTrackingRepository.findById(cravingTrackingId);

        if (existingTrackingOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy bản ghi theo dõi cơn thèm với ID: " + cravingTrackingId);
        }

        CravingTracking existingTracking = existingTrackingOptional.get();
        LocalDate today = LocalDate.now();

        // Nếu TrackTime đã vượt qua ngày hiện tại, không cho phép chỉnh sửa
        if (existingTracking.getTrackTime().toLocalDate().isBefore(today)) {
            throw new CravingTrackingEditForbiddenException("Không thể chỉnh sửa bản ghi theo dõi cho các ngày đã qua");
        }
        // Nếu TrackTime là cùng ngày với ngày hiện tại, cho phép chỉnh sửa
        // Không cập nhật CravingTrackingID, DailySummaryID, TrackTime
        existingTracking.setSmokedCount(updatedCravingTracking.getSmokedCount());
        existingTracking.setCravingsCount(updatedCravingTracking.getCravingsCount());
        existingTracking.setSituation(updatedCravingTracking.getSituation());
        existingTracking.setWithWhom(updatedCravingTracking.getWithWhom());

        //Tự động xóa bản ghi nếu cả smokedCount và cravingsCount đều bằng 0
        if (existingTracking.getSmokedCount() == 0 && existingTracking.getCravingsCount() == 0) {
            cravingTrackingRepository.delete(existingTracking);
            throw new CravingTrackingDeletedException("Bản ghi theo dõi cơn thèm đã được xóa vì số lượng thuốc hút và số lần thèm thuốc đều bằng 0");
        }

        // Các Bean Validation khác trên entity sẽ được áp dụng tự động khi save
        return cravingTrackingRepository.save(existingTracking);
    }

    //service khác...
}
