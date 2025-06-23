package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import com.swp391project.SWP391_QuitSmoking_BE.repository.PlanTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanTypeService {

    @Autowired
    private PlanTypeRepository planTypeRepository;

    public List<PlanType> getAllPlanTypes() {
        return planTypeRepository.findAll();
    }

    public Optional<PlanType> getPlanTypeById(String id) {
        return planTypeRepository.findById(id);
    }

    public PlanType createPlanType(PlanType planType) {
        return planTypeRepository.save(planType);
    }

    public PlanType updatePlanType(String id, PlanType planTypeDetails) {
        return planTypeRepository.findById(id).map(planType -> {
            planType.setPlanName(planTypeDetails.getPlanName());
            planType.setDescription(planTypeDetails.getDescription());
            planType.setDuration(planTypeDetails.getDuration());
            planType.setDurationType(planTypeDetails.getDurationType());

            return planTypeRepository.save(planType);
        }).orElseThrow(() -> new RuntimeException("PlanType not found with id " + id));
    }

    public void deletePlanType(String id) {
        if (!planTypeRepository.existsById(id)) {
            throw new RuntimeException("PlanType not found with id " + id);
        }
        planTypeRepository.deleteById(id);
    }
}