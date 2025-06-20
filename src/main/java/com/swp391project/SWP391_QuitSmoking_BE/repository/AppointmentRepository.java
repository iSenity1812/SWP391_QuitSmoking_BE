package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Appointment;
import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Lấy tất cả lịch hẹn của một Member, có phân trang và lọc theo khoảng ngày
    Page<Appointment> findByMember_MemberIdAndCoachSchedule_ScheduleDateBetweenOrderByCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
            UUID memberId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Lấy tất cả lịch hẹn của một Coach, có phân trang và lọc theo khoảng ngày và trạng thái
    Page<Appointment> findByCoachSchedule_Coach_CoachIdAndCoachSchedule_ScheduleDateBetweenAndStatusInOrderByCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
            UUID coachId, LocalDate startDate, LocalDate endDate, List<AppointmentStatus> statuses, Pageable pageable);

    // Lấy các lịch hẹn sắp tới của một Member ( chưa COMPLETED/CANCELLED/MISSED)
    // Sắp xếp theo ngày lịch trình và thời gian bắt đầu tăng dần, chỉ lấy những lịch ở tương lai/hiện tại
    List<Appointment> findByMember_MemberIdAndCoachSchedule_ScheduleDateGreaterThanEqualAndStatusInOrderByCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
            UUID memberId, LocalDate currentDate, List<AppointmentStatus> statuses, Pageable pageable);

    // Lấy các lịch hẹn sắp tới của một Coach  RESCHEDULED, chưa COMPLETED/CANCELLED/MISSED)
    List<Appointment> findByCoachSchedule_Coach_CoachIdAndCoachSchedule_ScheduleDateGreaterThanEqualAndStatusInOrderByCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
            UUID coachId, LocalDate currentDate, List<AppointmentStatus> statuses, Pageable pageable);

    // Lấy tất cả lịch hẹn (cho Admin), có phân trang và lọc theo khoảng ngày và trạng thái
    Page<Appointment> findByCoachSchedule_ScheduleDateBetweenAndStatusInOrderByCoachSchedule_Coach_FullNameAscCoachSchedule_ScheduleDateAscCoachSchedule_TimeSlot_StartTimeAsc(
            LocalDate startDate, LocalDate endDate, List<AppointmentStatus> statuses, Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.coachSchedule cs " +
            "JOIN FETCH cs.coach c " +
            "JOIN FETCH c.user " +
            "JOIN FETCH a.member m " +
            "JOIN FETCH m.user " +
            "WHERE cs.coach.coachId = :coachId AND cs.scheduleDate >= :date AND a.status IN :statuses " +
            "ORDER BY cs.scheduleDate ASC, cs.timeSlot.startTime ASC")
    List<Appointment> findUpcomingAppointmentsByCoachIdWithDetails(
            @Param("coachId") UUID coachId,
            @Param("date") LocalDate date,
            @Param("statuses") List<AppointmentStatus> statuses,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.coachSchedule cs " +
            "JOIN FETCH cs.coach c " +
            "JOIN FETCH c.user u " + // Nếu bạn cần thông tin User của Coach
            "WHERE c.coachId = :coachId " +
            "AND cs.scheduleDate BETWEEN :startDate AND :endDate " +
            "AND a.status IN :statuses")
    Page<Appointment> findCoachAppointments(
            @Param("coachId") UUID coachId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<AppointmentStatus> statuses,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.coachSchedule cs " +
            "JOIN FETCH cs.coach c " +
            "JOIN FETCH cs.timeSlot ts " +
            "WHERE a.member.memberId = :memberId " +
            "AND cs.scheduleDate BETWEEN :startDate AND :endDate")
    Page<Appointment> findWithDetailsByMemberIdAndDateRange(
            @Param("memberId") UUID memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );


//    @EntityGraph(value = "appointment.with.member.user.coachSchedule.coach.user.timeSlot", type = EntityGraph.EntityGraphType.LOAD)
//    List<Appointment> findByMember_MemberId(UUID memberId);
//
//    @EntityGraph(value = "appointment.with.member.user.coachSchedule.coach.user.timeSlot", type = EntityGraph.EntityGraphType.LOAD)
//    List<Appointment> findByCoachSchedule_Coach_CoachId(UUID coachId);
//
//    @EntityGraph(value = "appointment.with.member.user.coachSchedule.coach.user.timeSlot", type = EntityGraph.EntityGraphType.LOAD)
//    List<Appointment> findByStatusAndCoachSchedule_ScheduleDateGreaterThanEqual(AppointmentStatus status, LocalDate date);
//
//    // Phương thức tìm kiếm lịch hẹn sắp tới của coach
//    @EntityGraph(value = "appointment.with.member.user.coachSchedule.coach.user.timeSlot", type = EntityGraph.EntityGraphType.LOAD)
//    List<Appointment> findByCoachSchedule_Coach_CoachIdAndStatusInAndCoachSchedule_ScheduleDateGreaterThanEqualOrderByCoachSchedule_ScheduleDateAsc(
//            UUID coachId,
//            List<AppointmentStatus> statuses,
//            LocalDate date,
//            Pageable pageable);
}
