package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.service.QuitPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quitplans")
public class QuitPlanAPI {
    @Autowired
    private QuitPlanService quitPlanService;

    @GetMapping
    public List<QuitPlan> getAllQuitPlans() {
        return quitPlanService.getAllQuitPlans();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuitPlan> getQuitPlanById(@PathVariable Integer id) {
        Optional<QuitPlan> quitPlan = quitPlanService.getQuitPlanById(id);
        return quitPlan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuitPlan> createQuitPlan(@Valid @RequestBody QuitPlan quitPlan) {
        QuitPlan created = quitPlanService.createQuitPlan(quitPlan);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuitPlan> updateQuitPlan(@PathVariable Integer id, @Valid @RequestBody QuitPlan quitPlan) {
        try {
            QuitPlan updated = quitPlanService.updateQuitPlan(id, quitPlan);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuitPlan(@PathVariable Integer id) {
        quitPlanService.deleteQuitPlan(id);
        return ResponseEntity.noContent().build();
    }
}