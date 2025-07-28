package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.QuizCreationRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.QuizResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.tip.TipCreationRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.tip.TipResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.QuizService;
import com.swp391project.SWP391_QuitSmoking_BE.service.TipService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List; // Đảm bảo có import List
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class TaskController {

    private final QuizService quizService;
    private final TipService tipService;

    // --- API TẠO ---
    @PostMapping("/admin/quizzes")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> createQuiz(
            @Valid @RequestBody QuizCreationRequestDTO request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        QuizResponseDTO newQuiz = quizService.createQuiz(request, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newQuiz, "Quiz đã được tạo thành công."));
    }

    @PostMapping("/admin/tips")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<TipResponseDTO>> createTipByAdmin(
            @Valid @RequestBody TipCreationRequestDTO request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TipResponseDTO newTip = tipService.createTip(request, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newTip, "Tip đã được tạo thành công bởi Admin/Content Admin."));
    }

    @PostMapping("/tips")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER') or hasRole('COACH')")
    public ResponseEntity<ApiResponse<TipResponseDTO>> createTipByUser(
            @Valid @RequestBody TipCreationRequestDTO request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TipResponseDTO newTip = tipService.createTip(request, currentUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newTip, "Tip đã được tạo thành công bởi người dùng."));
    }

    // --- API XEM DANH SÁCH & CHI TIẾT QUIZ ---
    // Get all Quizzes: Admin có thể xem tất cả, User (Member/Coach) có thể xem list Quiz đã publish/active
    // Tạm thời cho phép tất cả các role có thể xem danh sách quiz.
    // Nếu có trường `isPublished` trong Quiz, bạn có thể lọc ở service.
    @GetMapping("/quizzes")
    @PreAuthorize("hasRole('SUPER_ADMIN') " +
            "or hasRole('CONTENT_ADMIN') " +
            "or hasRole('NORMAL_MEMBER') " +
            "or hasRole('PREMIUM_MEMBER') " +
            "or hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<QuizResponseDTO>>> getAllQuizzes() {
        List<QuizResponseDTO> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success(quizzes, "Lấy danh sách Quiz thành công."));
    }

    // Get Quiz by ID: Cho phép Admin và các thành viên khác xem chi tiết từng Quiz
    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> getQuizById(@PathVariable UUID quizId) {
        QuizResponseDTO quiz = quizService.getQuizById(quizId);
        return ResponseEntity.ok(ApiResponse.success(quiz, "Lấy thông tin Quiz thành công."));
    }

    // --- API XEM DANH SÁCH & CHI TIẾT TIP ---
    // Get all Tips: Tương tự như Quiz, cho phép các role xem danh sách Tip
    @GetMapping("/tips")
    @PreAuthorize("hasRole('SUPER_ADMIN') " +
            "or hasRole('CONTENT_ADMIN') " +
            "or hasRole('NORMAL_MEMBER') " +
            "or hasRole('PREMIUM_MEMBER') " +
            "or hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<TipResponseDTO>>> getAllTips() {
        List<TipResponseDTO> tips = tipService.getAllTips();
        return ResponseEntity.ok(ApiResponse.success(tips, "Lấy danh sách Tip thành công."));
    }

    // Get Tip by ID: Cho phép tất cả các role xem chi tiết Tip
    // (Đã có sẵn phương thức getTipById, chỉ cần thêm endpoint)
    @GetMapping("/tips/{tipId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<TipResponseDTO>> getTipById(@PathVariable UUID tipId) {
        TipResponseDTO tip = tipService.getTipById(tipId);
        return ResponseEntity.ok(ApiResponse.success(tip, "Lấy thông tin Tip thành công."));
    }

    // --- API CẬP NHẬT QUIZ ---
    @PutMapping("/admin/quizzes/{quizId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> updateQuiz(
            @PathVariable UUID quizId,
            @Valid @RequestBody QuizCreationRequestDTO request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        QuizResponseDTO updatedQuiz = quizService.updateQuiz(quizId, request, currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(updatedQuiz, "Quiz đã được cập nhật thành công."));
    }

    // --- API XÓA QUIZ ---
    @DeleteMapping("/admin/quizzes/{quizId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(
            @PathVariable UUID quizId,
            Authentication authentication) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success(null, "Quiz đã được xóa thành công."));
    }

    // --- API CẬP NHẬT TIP ---
    @PutMapping("/admin/tips/{tipId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<TipResponseDTO>> updateTip(
            @PathVariable UUID tipId,
            @Valid @RequestBody TipCreationRequestDTO request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TipResponseDTO updatedTip = tipService.updateTip(tipId, request, currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(updatedTip, "Tip đã được cập nhật thành công."));
    }

    // --- API XÓA TIP ---
    @DeleteMapping("/admin/tips/{tipId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTip(
            @PathVariable UUID tipId,
            Authentication authentication) {
        tipService.deleteTip(tipId);
        return ResponseEntity.ok(ApiResponse.success(null, "Tip đã được xóa thành công."));
    }

    // --- API IMPORT QUIZ TỪ EXCEL ---
    @PostMapping("/admin/quizzes/import")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<String>> importQuizzes(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        // log role của người dùng hiện tại
        System.out.println("Current user role: " + currentUser.getRole());
        quizService.importQuizzesFromExcel(file, currentUser.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Import quizzes từ Excel thành công."));
    }
}