package com.swp391project.SWP391_QuitSmoking_BE;

import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.HealthMetric;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.HealthMetricType;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.HealthMetricRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import com.swp391project.SWP391_QuitSmoking_BE.service.HealthMetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthMetricServiceTest {

    @Mock
    private HealthMetricRepository healthMetricRepository;

    @Mock
    private QuitPlanRepository quitPlanRepository;

    @Mock
    private DailySummaryRepository dailySummaryRepository;

    @InjectMocks
    private HealthMetricService healthMetricService;

    private User testUser;
    private QuitPlan testQuitPlan;
    private HealthMetric testHealthMetric;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setUsername("testuser");

        testQuitPlan = new QuitPlan();
        testQuitPlan.setQuitPlanId(1);
        testQuitPlan.setStatus(QuitPlanStatus.IN_PROGRESS);
        testQuitPlan.setStartDate(LocalDateTime.now().minusDays(1)); // Bắt đầu 1 ngày trước

        testHealthMetric = HealthMetric.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .metricType(HealthMetricType.PULSE_RATE)
                .currentProgress(0.0)
                .isCompleted(false)
                .hasRegressed(false)
                .description("Test description")
                .build();
    }

    @Test
    void testInitializeHealthMetrics() {
        // Given
        when(healthMetricRepository.existsByUserAndMetricType(any(), any())).thenReturn(false);
        when(healthMetricRepository.save(any())).thenReturn(testHealthMetric);

        // When
        healthMetricService.initializeHealthMetrics(testUser);

        // Then
        verify(healthMetricRepository, times(HealthMetricType.values().length))
                .existsByUserAndMetricType(eq(testUser), any(HealthMetricType.class));
        verify(healthMetricRepository, times(HealthMetricType.values().length))
                .save(any(HealthMetric.class));
    }

    @Test
    void testUpdateHealthMetricsProgress() {
        // Given
        Member testMember = new Member();
        testMember.setMemberId(UUID.randomUUID());
        testUser.setMember(testMember);
        when(quitPlanRepository.findActiveQuitPlanByMember(testMember)).thenReturn(Optional.of(testQuitPlan));
        when(healthMetricRepository.findByUserOrderByMetricType(testUser))
                .thenReturn(Arrays.asList(testHealthMetric));
        when(healthMetricRepository.save(any())).thenReturn(testHealthMetric);
        when(dailySummaryRepository.findByQuitPlan(testQuitPlan)).thenReturn(Arrays.asList());

        // When
        healthMetricService.updateHealthMetricsProgress(testUser);

        // Then
        verify(quitPlanRepository).findActiveQuitPlanByMember(testMember);
        verify(healthMetricRepository).findByUserOrderByMetricType(testUser);
        verify(healthMetricRepository).save(any(HealthMetric.class));
        verify(dailySummaryRepository).findByQuitPlan(testQuitPlan);
    }

    @Test
    void testUpdateHealthMetricsProgress_WithSmokingDays() {
        // Given
        Member testMember = new Member();
        testMember.setMemberId(UUID.randomUUID());
        testUser.setMember(testMember);
        when(quitPlanRepository.findActiveQuitPlanByMember(testMember)).thenReturn(Optional.of(testQuitPlan));
        when(healthMetricRepository.findByUserOrderByMetricType(testUser))
                .thenReturn(Arrays.asList(testHealthMetric));
        when(healthMetricRepository.save(any())).thenReturn(testHealthMetric);
        
        // Create smoking daily summaries
        DailySummary smokingSummary1 = new DailySummary();
        smokingSummary1.setTotalSmokedCount(5);
        DailySummary smokingSummary2 = new DailySummary();
        smokingSummary2.setTotalSmokedCount(3);
        DailySummary nonSmokingSummary = new DailySummary();
        nonSmokingSummary.setTotalSmokedCount(0);
        
        when(dailySummaryRepository.findByQuitPlan(testQuitPlan))
                .thenReturn(Arrays.asList(smokingSummary1, smokingSummary2, nonSmokingSummary));

        // When
        healthMetricService.updateHealthMetricsProgress(testUser);

        // Then
        verify(quitPlanRepository).findActiveQuitPlanByMember(testMember);
        verify(healthMetricRepository).findByUserOrderByMetricType(testUser);
        verify(healthMetricRepository).save(any(HealthMetric.class));
        verify(dailySummaryRepository).findByQuitPlan(testQuitPlan);
    }

    @Test
    void testUpdateHealthMetricsProgress_NoQuitPlan() {
        // Given
        Member testMember = new Member();
        testMember.setMemberId(UUID.randomUUID());
        testUser.setMember(testMember);
        when(quitPlanRepository.findActiveQuitPlanByMember(testMember)).thenReturn(Optional.empty());

        // When
        healthMetricService.updateHealthMetricsProgress(testUser);

        // Then
        verify(quitPlanRepository).findActiveQuitPlanByMember(testMember);
        verify(healthMetricRepository, never()).findByUserOrderByMetricType(any());
        verify(dailySummaryRepository, never()).findByQuitPlan(any());
    }

    @Test
    void testGetUserHealthMetrics() {
        // Given
        List<HealthMetric> metrics = Arrays.asList(testHealthMetric);
        when(healthMetricRepository.findByUserOrderByMetricType(testUser)).thenReturn(metrics);

        // When
        var result = healthMetricService.getUserHealthMetrics(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(healthMetricRepository).findByUserOrderByMetricType(testUser);
    }

    @Test
    void testGetHealthOverview() {
        // Given
        Member testMember = new Member();
        testMember.setMemberId(UUID.randomUUID());
        testUser.setMember(testMember);
        when(quitPlanRepository.findActiveQuitPlanByMember(testMember)).thenReturn(Optional.of(testQuitPlan));
        when(healthMetricRepository.findByUserOrderByMetricType(testUser))
                .thenReturn(Arrays.asList(testHealthMetric));
        when(healthMetricRepository.countTotalMetricsByUser(testUser)).thenReturn(1L);
        when(healthMetricRepository.countCompletedMetricsByUser(testUser)).thenReturn(0L);
        when(healthMetricRepository.findTopProgressMetricsByUser(testUser)).thenReturn(Arrays.asList());
        when(healthMetricRepository.findIncompleteMetricsByUserOrderByTargetHours(testUser)).thenReturn(Arrays.asList());
        when(healthMetricRepository.findCompletedMetricsByUserOrderByAchievedDate(testUser)).thenReturn(Arrays.asList());
        when(healthMetricRepository.save(any())).thenReturn(testHealthMetric);
        when(dailySummaryRepository.findByQuitPlan(testQuitPlan)).thenReturn(Arrays.asList());

        // When
        var result = healthMetricService.getHealthOverview(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTotalMetrics());
        assertEquals(0L, result.getCompletedMetrics());
        verify(healthMetricRepository).countTotalMetricsByUser(testUser);
        verify(healthMetricRepository).countCompletedMetricsByUser(testUser);
        verify(dailySummaryRepository).findByQuitPlan(testQuitPlan);
    }

    @Test
    void testGetHealthMetricByType() {
        // Given
        when(healthMetricRepository.findByUserAndMetricType(testUser, HealthMetricType.PULSE_RATE))
                .thenReturn(Optional.of(testHealthMetric));

        // When
        var result = healthMetricService.getHealthMetricByType(testUser, HealthMetricType.PULSE_RATE);

        // Then
        assertTrue(result.isPresent());
        assertEquals(HealthMetricType.PULSE_RATE, result.get().getMetricType());
        verify(healthMetricRepository).findByUserAndMetricType(testUser, HealthMetricType.PULSE_RATE);
    }

    @Test
    void testGetHealthMetricByType_NotFound() {
        // Given
        when(healthMetricRepository.findByUserAndMetricType(testUser, HealthMetricType.PULSE_RATE))
                .thenReturn(Optional.empty());

        // When
        var result = healthMetricService.getHealthMetricByType(testUser, HealthMetricType.PULSE_RATE);

        // Then
        assertFalse(result.isPresent());
        verify(healthMetricRepository).findByUserAndMetricType(testUser, HealthMetricType.PULSE_RATE);
    }
} 