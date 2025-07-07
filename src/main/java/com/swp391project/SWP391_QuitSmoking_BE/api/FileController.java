package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.FileUploadService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload-image")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        String imageUrl = fileUploadService.uploadImage(file);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("message", "Tải ảnh lên thành công");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Tải ảnh lên thành công."));
    }

    @DeleteMapping("/delete-image")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @RequestParam("imageUrl") String imageUrl) {

        fileUploadService.deleteImage(imageUrl);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Xóa ảnh thành công."));
    }
}
