package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CoachSchedule;
import com.swp391project.SWP391_QuitSmoking_BE.entity.TimeSlot;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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

    // Query để lấy tất cả CoachSchedule cho một Coach trong một khoảng thời gian
    // và fetch eagerly các mối quan hệ cần thiết để tránh N+1
    @Query("SELECT cs FROM CoachSchedule cs " +
            "LEFT JOIN FETCH cs.timeSlot ts " +
            "LEFT JOIN FETCH cs.appointments apt " +
            "LEFT JOIN FETCH apt.member m " +
            "LEFT JOIN FETCH m.user u_member " +
            "WHERE cs.coach.coachId = :coachId " +
            "AND cs.scheduleDate BETWEEN :startDate AND :endDate " +
            "AND cs.isDeleted = FALSE " + // Loại bỏ các lịch đã bị xóa mềm
            "ORDER BY cs.scheduleDate ASC, ts.startTime ASC")
    List<CoachSchedule> findByCoachIdAndScheduleDateBetweenWithDetails(
            @Param("coachId") UUID coachId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT cs FROM CoachSchedule cs " +
            "JOIN FETCH cs.timeSlot ts " +
            "JOIN FETCH cs.coach c " +
            "JOIN FETCH c.user u " +
            "LEFT JOIN cs.appointments apt " + // LEFT JOIN để lấy tất cả schedules, dù có appointment hay không
            "WHERE cs.isDeleted = false " + // Loại bỏ các lịch đã bị xóa mềm
            "AND cs.scheduleDate = :date " + // Theo ngày cụ thể
            "AND ts.startTime >= :startTime " + // Giờ bắt đầu của timeslot >= giờ bắt đầu yêu cầu
            "AND ts.endTime <= :endTime " + // Giờ kết thúc của timeslot <= giờ kết thúc yêu cầu
            "AND NOT EXISTS (" + // Subquery để loại trừ các schedule có cuộc hẹn CONFIRMED/PENDING
            "   SELECT a FROM Appointment a " +
            "   WHERE a.coachSchedule.scheduleId = cs.scheduleId " + //
            "   AND (a.status = 'CONFIRMED' OR a.status = 'COMPLTETED')" + //
            ") " +
            "ORDER BY cs.scheduleDate ASC, ts.startTime ASC, c.fullName ASC")
    List<CoachSchedule> findAvailableSchedulesByDateAndTimeRange(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT DISTINCT cs FROM CoachSchedule cs " +
            "JOIN FETCH cs.timeSlot ts " +
            "JOIN FETCH cs.coach c " +
            "JOIN FETCH c.user u " +
            "LEFT JOIN cs.appointments apt " +
            "WHERE cs.isDeleted = false " + // Loại bỏ các lịch đã bị xóa mềm
            "AND cs.scheduleDate BETWEEN :startDate AND :endDate " + // Theo khoảng ngày
            "AND ts.startTime >= :startTime " + // Giờ bắt đầu của timeslot >= giờ bắt đầu yêu cầu
            "AND ts.endTime <= :endTime " + // Giờ kết thúc của timeslot <= giờ kết thúc yêu cầu
            "AND NOT EXISTS (" + // Subquery để loại trừ các schedule có cuộc hẹn CONFIRMED/PENDING
            "   SELECT a FROM Appointment a " +
            "   WHERE a.coachSchedule.scheduleId = cs.scheduleId " + //
            "   AND (a.status = 'CONFIRMED' OR a.status = 'PENDING')" + //
            ") " +
            "ORDER BY cs.scheduleDate ASC, ts.startTime ASC, c.fullName ASC")
    List<CoachSchedule> findAvailableSchedulesByDateRangeAndTimeRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    Optional<CoachSchedule> findByCoachAndScheduleDateAndTimeSlotAndIsDeletedFalse(Coach coach, @NotNull(message = "Schedule date không thể null") @FutureOrPresent(message = "Schedule date must be today or in the future") LocalDate scheduleDate, TimeSlot timeSlot);


    // Phương thức để lấy tất cả các TimeSlot (để Frontend hiển thị lựa chọn)
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.isDeleted = FALSE")
    List<TimeSlot> findAllActiveTimeSlots();


    // Phương thức tìm kiếm các lịch trình trống dựa trên khoảng ngày và timeSlotIds
    @Query("SELECT cs FROM CoachSchedule cs " +
            "JOIN FETCH cs.coach c " +      // Lấy Coach cùng lúc
            "JOIN FETCH c.user u " +        // Lấy User của Coach cùng lúc
            "JOIN FETCH cs.timeSlot ts " +  // Lấy TimeSlot cùng lúc
            "WHERE cs.isBooked = FALSE " +  // Lịch trình chưa được đặt
            "AND cs.isDeleted = FALSE " +   // Lịch trình chưa bị xóa
            "AND cs.scheduleDate BETWEEN :startDate AND :endDate " + // Trong khoảng ngày
            "AND (:timeSlotIds IS NULL OR ts.timeSlotId IN :timeSlotIds)") // Lọc theo timeSlotIds nếu được cung cấp
    List<CoachSchedule> findAvailableSchedulesByDateRangeAndTimeslots(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("timeSlotIds") List<Integer> timeSlotIds
    );

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
