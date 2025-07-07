package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.exception.AppException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.max-size:5242880}") // 5MB default
    private long maxFileSize;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    /**
     * Upload file và trả về URL của file đã upload
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        try {
            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file unique
            String uniqueFileName = generateUniqueFileName(originalFilename);
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Copy file vào thư mục upload
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL của file (có thể là relative path hoặc full URL tùy theo cấu hình)
            return "/uploads/" + uniqueFileName;

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xóa file đã upload
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Extract filename from URL
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Deleted file: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            // Không throw exception vì việc xóa file không thành công không nên làm fail toàn bộ operation
        }
    }

    /**
     * Tạo tên file unique bằng cách thêm UUID
     */
    private String generateUniqueFileName(String originalFilename) {
        String fileExtension = getFileExtension(originalFilename);
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));

        // Làm sạch tên file
        baseName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");

        return baseName + "_" + UUID.randomUUID().toString() + fileExtension;
    }

    /**
     * Lấy extension của file
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * Validate image file
     */
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String fileExtension = getFileExtension(originalFilename);
        return ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }
}
