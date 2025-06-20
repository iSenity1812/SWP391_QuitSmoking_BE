package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import com.swp391project.SWP391_QuitSmoking_BE.service.PlanTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/plantypes")
public class PlanTypeAPI {
    @Autowired
    private PlanTypeService planTypeService;

    @GetMapping
    public List<PlanType> getAllPlanTypes() {
        return planTypeService.getAllPlanTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanType> getPlanTypeById(@PathVariable String id) {
        Optional<PlanType> planType = planTypeService.getPlanTypeById(id);
        return planType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlanType> createPlanType(@Valid @RequestBody PlanType planType) {
        PlanType created = planTypeService.createPlanType(planType);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanType> updatePlanType(@PathVariable String id, @Valid @RequestBody PlanType planType) {
        try {
            PlanType updated = planTypeService.updatePlanType(id, planType);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanType(@PathVariable String id) {
        planTypeService.deletePlanType(id);
        return ResponseEntity.noContent().build();
    }
}