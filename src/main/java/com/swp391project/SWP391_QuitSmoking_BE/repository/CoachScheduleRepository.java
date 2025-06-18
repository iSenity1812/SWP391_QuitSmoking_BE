package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CoachSchedule;
import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoachScheduleRepository extends JpaRepository<CoachSchedule, Long> {
    // Tìm lịch của một Coach theo ngày và timeslot (để kiểm tra trùng lặp trước khi tạo)
    // Sẽ dùng coach.coachId để truy vấn
    Optional<CoachSchedule> findByCoachAndScheduleDateAndTimeSlot(Coach coach, LocalDate scheduleDate, TimeSlot timeSlot);

    // Lấy tất cả lịch của một Coach (bao gồm cả đã đặt và chưa đặt)
    List<CoachSchedule> findByCoach_CoachIdOrderByScheduleDateAscTimeSlot_StartTimeAsc(UUID coachId);

    // Lấy lịch trống của một Coach trong một khoảng thời gian
    List<CoachSchedule> findByCoach_CoachIdAndIsBookedFalseAndScheduleDateBetweenOrderByScheduleDateAscTimeSlot_StartTimeAsc(
            UUID coachId, LocalDate startDate, LocalDate endDate);

    // Lấy lịch trống của TẤT CẢ Coach trong một khoảng thời gian (cho admin/tìm kiếm chung)
    List<CoachSchedule> findByIsBookedFalseAndScheduleDateBetweenOrderByCoach_FullNameAscScheduleDateAscTimeSlot_StartTimeAsc(
            LocalDate startDate, LocalDate endDate);

    // Lấy lịch đã đặt của một Coach (để hiển thị các buổi hẹn đã confirm)
    List<CoachSchedule> findByCoach_CoachIdAndIsBookedTrueOrderByScheduleDateAscTimeSlot_StartTimeAsc(UUID coachId);

    // Phương thức để tìm CoachSchedule theo ID và đảm bảo nó chưa được đặt (cho bước đặt lịch trong AppointmentService)
    Optional<CoachSchedule> findByScheduleIdAndIsBookedFalse(Long scheduleId);

    // Lấy lịch của một Coach trong một khoảng thời gian CÓ PHÂN TRANG (bao gồm cả đã đặt và chưa đặt)
    Page<CoachSchedule> findByCoach_CoachIdAndScheduleDateBetweenOrderByScheduleDateAscTimeSlot_StartTimeAsc(
            UUID coachId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Lấy lịch trống của một Coach trong một khoảng thời gian CÓ PHÂN TRANG
    Page<CoachSchedule> findByCoach_CoachIdAndIsBookedFalseAndScheduleDateBetweenOrderByScheduleDateAscTimeSlot_StartTimeAsc(
            UUID coachId, LocalDate startDate, LocalDate endDate, Pageable pageable);


    // Lấy lịch của một Coach cho một ngày CỤ THỂ CÓ PHÂN TRANG
    Page<CoachSchedule> findByCoach_CoachIdAndScheduleDateOrderByTimeSlot_StartTimeAsc(
            UUID coachId, LocalDate scheduleDate, Pageable pageable);

    // Lấy lịch trống của một Coach cho một ngày CỤ THỂ CÓ PHÂN TRANG
    Page<CoachSchedule> findByCoach_CoachIdAndIsBookedFalseAndScheduleDateOrderByTimeSlot_StartTimeAsc(
            UUID coachId, LocalDate scheduleDate, Pageable pageable);

    // Lấy tất cả lịch trống (của mọi Coach) trong một khoảng thời gian CÓ PHÂN TRANG
    Page<CoachSchedule> findByIsBookedFalseAndScheduleDateBetweenOrderByCoach_FullNameAscScheduleDateAscTimeSlot_StartTimeAsc(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Lấy tất cả lịch trống (của mọi Coach) cho một ngày CỤ THỂ CÓ PHÂN TRANG
    Page<CoachSchedule> findByIsBookedFalseAndScheduleDateOrderByCoach_FullNameAscTimeSlot_StartTimeAsc(
            LocalDate scheduleDate, Pageable pageable);

    List<CoachSchedule> findByCoach_CoachIdAndIsBookedFalseAndScheduleDateGreaterThanEqualOrderByScheduleDateAscTimeSlot_StartTimeAsc(UUID coachId, LocalDate now, Pageable pageable);

    // Tìm lịch hẹn sắp tới của một Coach (1 hoặc 2 lịch gần nhất)
    // Sắp xếp theo ngày và giờ bắt đầu tăng dần, chỉ lấy những lịch chưa đặt và ở tương lai/hiện tại
//    List<CoachSchedule> findByCoach_CoachIdAndIsBookedFalseAndScheduleDateGreaterThanEqualOrderByScheduleDateAscTimeSlot_StartTimeAsc(
//            UUID coachId, LocalDate currentDate, Pageable pageable);
//
//    @EntityGraph(value = "coachSchedule.with.coach.user.timeSlot", type = EntityGraph.EntityGraphType.LOAD)
//    List<CoachSchedule> findByCoach_CoachId(UUID coachId);
//
//    @EntityGraph(value = "coachSchedule.with.coach.user.timeSlot", type = EntityGraph.EntityGraphType.LOAD)
//    List<CoachSchedule> findByCoach_CoachIdAndScheduleDateBetweenAndIsBookedFalse(
//            UUID coachId, LocalDate startDate, LocalDate endDate);
}
