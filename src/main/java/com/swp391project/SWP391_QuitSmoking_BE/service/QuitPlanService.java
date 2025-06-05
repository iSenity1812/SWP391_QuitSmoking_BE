package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuitPlanService {
    @Autowired
    private QuitPlanRepository quitPlanRepository;

    public List<QuitPlan> getAllQuitPlans() {
        return quitPlanRepository.findAll();
    }

    public Optional<QuitPlan> getQuitPlanById(Integer id) {
        return quitPlanRepository.findById(id);
    }

    public QuitPlan createQuitPlan(QuitPlan quitPlan) {
        return quitPlanRepository.save(quitPlan);
    }

    public QuitPlan updateQuitPlan(Integer id, QuitPlan quitPlanDetails) {
        return quitPlanRepository.findById(id).map(quitPlan -> {
            quitPlan.setMember(quitPlanDetails.getMember());
            quitPlan.setPlanType(quitPlanDetails.getPlanType());
            quitPlan.setReductionType(quitPlanDetails.getReductionType());
            quitPlan.setCreatedAt(quitPlanDetails.getCreatedAt());
            quitPlan.setStartDate(quitPlanDetails.getStartDate());
            quitPlan.setGoalDate(quitPlanDetails.getGoalDate());
            quitPlan.setInitialSmokingAmount(quitPlanDetails.getInitialSmokingAmount());
            quitPlan.setStatus(quitPlanDetails.getStatus());
            return quitPlanRepository.save(quitPlan);
        }).orElseThrow(() -> new RuntimeException("QuitPlan not found"));
    }

    public void deleteQuitPlan(Integer id) {
        quitPlanRepository.deleteById(id);
    }
}