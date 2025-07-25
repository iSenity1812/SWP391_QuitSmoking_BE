package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.WeeklyGoal;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, Long> {

    /**
     * Tìm tất cả weekly goals của một quit plan
     */
    List<WeeklyGoal> findByQuitPlanOrderByWeekStartDateDesc(QuitPlan quitPlan);

    /**
     * Tìm weekly goal hiện tại (tuần này)
     */
    @Query("SELECT wg FROM WeeklyGoal wg WHERE wg.quitPlan = :quitPlan " +
           "AND :currentDate BETWEEN wg.weekStartDate AND wg.weekEndDate")
    Optional<WeeklyGoal> findCurrentWeeklyGoal(@Param("quitPlan") QuitPlan quitPlan, 
                                              @Param("currentDate") LocalDate currentDate);

    /**
     * Tìm weekly goal gần nhất của một quit plan
     */
    Optional<WeeklyGoal> findFirstByQuitPlanOrderByWeekStartDateDesc(QuitPlan quitPlan);

    /**
     * Tìm weekly goals trong khoảng thời gian
     */
    @Query("SELECT wg FROM WeeklyGoal wg WHERE wg.quitPlan = :quitPlan " +
           "AND wg.weekStartDate >= :startDate AND wg.weekEndDate <= :endDate " +
           "ORDER BY wg.weekStartDate ASC")
    List<WeeklyGoal> findWeeklyGoalsInPeriod(@Param("quitPlan") QuitPlan quitPlan,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * Tìm tất cả weekly goals đã hoàn thành của một quit plan
     */
    List<WeeklyGoal> findByQuitPlanAndIsAchievedTrueOrderByCompletedAtDesc(QuitPlan quitPlan);

    /**
     * Tìm weekly goals chưa hoàn thành
     */
    List<WeeklyGoal> findByQuitPlanAndIsAchievedFalseOrderByWeekStartDateAsc(QuitPlan quitPlan);

    /**
     * Đếm số weekly goals đã hoàn thành của một quit plan
     */
    @Query("SELECT COUNT(wg) FROM WeeklyGoal wg WHERE wg.quitPlan = :quitPlan AND wg.isAchieved = true")
    Long countAchievedGoals(@Param("quitPlan") QuitPlan quitPlan);

    /**
     * Tính tổng reward points của một quit plan
     */
    @Query("SELECT COALESCE(SUM(wg.rewardPoints), 0) FROM WeeklyGoal wg WHERE wg.quitPlan = :quitPlan")
    Integer getTotalRewardPoints(@Param("quitPlan") QuitPlan quitPlan);

    /**
     * Tìm weekly goals theo member ID
     */
    @Query("SELECT wg FROM WeeklyGoal wg WHERE wg.quitPlan.member.memberId = :memberId " +
           "ORDER BY wg.weekStartDate DESC")
    List<WeeklyGoal> findByMemberIdOrderByWeekStartDateDesc(@Param("memberId") UUID memberId);

    /**
     * Tìm weekly goal hiện tại theo member ID
     */
    @Query("SELECT wg FROM WeeklyGoal wg WHERE wg.quitPlan.member.memberId = :memberId " +
           "AND :currentDate BETWEEN wg.weekStartDate AND wg.weekEndDate " +
           "AND wg.quitPlan.status = 'IN_PROGRESS'")
    Optional<WeeklyGoal> findCurrentWeeklyGoalByMemberId(@Param("memberId") UUID memberId,
                                                        @Param("currentDate") LocalDate currentDate);

    /**
     * Tìm weekly goals cần update progress (trong tuần hiện tại)
     */
    @Query("SELECT wg FROM WeeklyGoal wg WHERE :currentDate BETWEEN wg.weekStartDate AND wg.weekEndDate " +
           "AND wg.isAchieved = false")
    List<WeeklyGoal> findActiveWeeklyGoals(@Param("currentDate") LocalDate currentDate);

    /**
     * Kiểm tra xem đã có weekly goal cho tuần này chưa
     */
    @Query("SELECT COUNT(wg) > 0 FROM WeeklyGoal wg WHERE wg.quitPlan = :quitPlan " +
           "AND wg.weekStartDate = :weekStartDate")
    boolean existsByQuitPlanAndWeekStartDate(@Param("quitPlan") QuitPlan quitPlan,
                                           @Param("weekStartDate") LocalDate weekStartDate);

    /**
     * Lấy streak (số tuần liên tiếp hoàn thành goal)
     */
    @Query(value = "SELECT COUNT(*) FROM (" +
                   "SELECT wg.weekly_goal_id, " +
                   "ROW_NUMBER() OVER (ORDER BY wg.week_start_date DESC) - " +
                   "ROW_NUMBER() OVER (PARTITION BY wg.is_achieved ORDER BY wg.week_start_date DESC) as grp " +
                   "FROM weekly_goal wg " +
                   "WHERE wg.quit_plan_id = :quitPlanId " +
                   "ORDER BY wg.week_start_date DESC" +
                   ") AS streak WHERE grp = 0 AND week_start_date IN (" +
                   "SELECT week_start_date FROM weekly_goal " +
                   "WHERE quit_plan_id = :quitPlanId AND is_achieved = true" +
                   ")", nativeQuery = true)
    Integer getCurrentStreak(@Param("quitPlanId") Integer quitPlanId);

    /**
     * Tìm tất cả weekly goals đã hoàn thành của một member
     */
    @Query("SELECT wg FROM WeeklyGoal wg WHERE wg.quitPlan.member = :member AND wg.isAchieved = true ORDER BY wg.completedAt DESC")
    List<WeeklyGoal> findCompletedWeeklyGoalsByMember(@Param("member") Member member);
}
