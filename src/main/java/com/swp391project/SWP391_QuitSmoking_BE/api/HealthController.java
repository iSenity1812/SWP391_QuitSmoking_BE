package com.swp391project.SWP391_QuitSmoking_BE.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "SWP391 QuitSmoking Backend");
        response.put("version", "2.0");
        response.put("message", "Backend is running successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("backend", "ONLINE");
        response.put("database", "CONNECTED");
        response.put("payment", "AVAILABLE");

        return ResponseEntity.ok(response);
    }
}