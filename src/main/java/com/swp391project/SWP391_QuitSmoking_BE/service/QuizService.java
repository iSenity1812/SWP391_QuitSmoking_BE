package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.*;
import com.swp391project.SWP391_QuitSmoking_BE.dto.option.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Option;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Quiz;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.OptionRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuizRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository; // Giữ lại nếu có nhu cầu thao tác trực tiếp với Option
    private final UserRepository userRepository;

    private static final int REQUIRED_NUMBER_OF_OPTIONS = 2;

    @Autowired
    public QuizService(QuizRepository quizRepository, OptionRepository optionRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
    }

    // Phương thức chuyển đổi Entity sang DTO
    private QuizResponseDTO convertToResponseDto(Quiz quiz) {
        List<OptionResponseDTO> optionDTOs = quiz.getOptions().stream()
                .map(option -> new OptionResponseDTO(option.getOptionId(), option.getContent(), option.getIsCorrect()))
                .collect(Collectors.toList());
        return new QuizResponseDTO(quiz.getQuizId(), quiz.getTitle(), quiz.getDescription(), optionDTOs);
    }

    // Phương thức chuyển đổi DTO sang Entity
    private Quiz convertToEntity(QuizCreationRequestDTO quizDto, User createdByAdmin) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setCreatedByAdmin(createdByAdmin);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        return quiz;
    }

    @Transactional
    public QuizResponseDTO createQuiz(QuizCreationRequestDTO request, UUID createdByUserId) {
        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User creating Quiz not found with ID: " + createdByUserId));

        // TẠM THỜI COMMENT ĐỂ TEST VỚI NORMAL_MEMBER - NHỚ BỎ COMMENT KHI TRIỂN KHAI THỰC TẾ
        if (creator.getRole() != Role.SUPER_ADMIN && creator.getRole() != Role.CONTENT_ADMIN) {
            throw new IllegalArgumentException("Chỉ quản trị viên (SUPER_ADMIN hoặc CONTENT_ADMIN) mới có quyền tạo Quiz.");
        }

        // Kiểm tra số lượng options và đảm bảo có ít nhất một đáp án đúng
        if (request.getOptions() == null || request.getOptions().size() < REQUIRED_NUMBER_OF_OPTIONS) {
            throw new IllegalArgumentException("Quiz phải có ít nhất " + REQUIRED_NUMBER_OF_OPTIONS + " lựa chọn.");
        }
        boolean hasCorrectOption = request.getOptions().stream().anyMatch(QuizCreationRequestDTO.OptionRequest::getIsCorrect);
        if (!hasCorrectOption) {
            throw new IllegalArgumentException("Quiz phải có ít nhất một lựa chọn đúng.");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        quiz.setCreatedByAdmin(creator);

        // Lưu Quiz trước để có quizId cho Options
        Quiz savedQuiz = quizRepository.save(quiz);

        Set<Option> options = request.getOptions().stream()
                .map(optionRequest -> {
                    Option option = new Option();
                    option.setContent(optionRequest.getContent());
                    option.setIsCorrect(optionRequest.getIsCorrect());
                    option.setQuiz(savedQuiz); // Liên kết với Quiz đã lưu
                    option.setCreatedAt(LocalDateTime.now());
                    option.setUpdatedAt(LocalDateTime.now());
                    option.setCreatedByAdmin(creator); // Người tạo Option là người tạo Quiz
                    return option;
                })
                .collect(Collectors.toSet());

        savedQuiz.setOptions(options);
        return convertToResponseDto(savedQuiz);
    }

    @Transactional
    public List<QuizResponseDTO> getAllQuizzes() {
        // Đảm bảo options được tải đủ để tránh LazyInitializationException nếu không có FetchType.EAGER
        // Hoặc thêm @EntityGraph vào repository method nếu bạn muốn load options eager
        return quizRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public QuizResponseDTO getQuizById(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        // Options đã được tải thông qua mối quan hệ trong Quiz entity,
        // nếu bạn đang trong một giao dịch, có thể truy cập trực tiếp.
        return convertToResponseDto(quiz);
    }

    // --- PHƯƠNG THỨC CẬP NHẬT QUIZ ---
    @Transactional
    public QuizResponseDTO updateQuiz(UUID quizId, QuizCreationRequestDTO request, UUID updatedByUserId) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        User updater = userRepository.findById(updatedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User updating Quiz not found with ID: " + updatedByUserId));

        // TẠM THỜI COMMENT ĐỂ TEST VỚI NORMAL_MEMBER - NHỚ BỎ COMMENT KHI TRIỂN KHAI THỰC TẾ
        if (updater.getRole() != Role.SUPER_ADMIN && updater.getRole() != Role.CONTENT_ADMIN) {
            throw new IllegalArgumentException("Chỉ quản trị viên (SUPER_ADMIN hoặc CONTENT_ADMIN) mới có quyền cập nhật Quiz.");
        }

        // Các kiểm tra validation cho dữ liệu mới (tương tự như khi tạo)
        // Kiểm tra số lượng options và đảm bảo có ít nhất một đáp án đúng
        if (request.getOptions() == null || request.getOptions().size() < REQUIRED_NUMBER_OF_OPTIONS) {
            throw new IllegalArgumentException("Quiz phải có ít nhất " + REQUIRED_NUMBER_OF_OPTIONS + " lựa chọn.");
        }
        boolean hasCorrectOption = request.getOptions().stream().anyMatch(QuizCreationRequestDTO.OptionRequest::getIsCorrect);
        if (!hasCorrectOption) {
            throw new IllegalArgumentException("Quiz phải có ít nhất một lựa chọn đúng.");
        }

        existingQuiz.setTitle(request.getTitle());
        existingQuiz.setDescription(request.getDescription());
        existingQuiz.setUpdatedAt(LocalDateTime.now());


        // 1. Xóa tất cả các options cũ khỏi collection của Quiz.
        //    Vì có orphanRemoval = true, các Option này sẽ được đánh dấu để xóa khỏi DB.
        existingQuiz.getOptions().clear();

        // 2. Thêm các options mới vào collection của Quiz.
        //    Vì có CascadeType.ALL, các Option này sẽ được PERSIST (lưu) vào DB.
        Set<Option> newOptions = new HashSet<>(); // Tạo một set mới để chứa options
        for (QuizCreationRequestDTO.OptionRequest optionRequest : request.getOptions()) {
            Option option = new Option();
            option.setContent(optionRequest.getContent());
            option.setIsCorrect(optionRequest.getIsCorrect());
            option.setQuiz(existingQuiz); // Liên kết với quiz hiện có
            option.setCreatedAt(LocalDateTime.now());
            option.setUpdatedAt(LocalDateTime.now());
            option.setCreatedByAdmin(updater); // Gán người tạo option (có thể là người cập nhật quiz)
            newOptions.add(option);
        }
        // Thêm tất cả options mới vào collection hiện có của Quiz.
        // Đây là bước quan trọng để Hibernate biết các option mới này thuộc về quiz này.
        existingQuiz.getOptions().addAll(newOptions);
        // --- KẾT THÚC ĐOẠN CODE SỬA LỖI ---


        Quiz updatedQuiz = quizRepository.save(existingQuiz); // Lưu Quiz, Hibernate sẽ xử lý options

        return convertToResponseDto(updatedQuiz);
    }

    // --- PHƯƠNG THỨC XÓA QUIZ ---
    @Transactional
    public void deleteQuiz(UUID quizId) {
        Quiz quizToDelete = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        // --- QUAN TRỌNG: NGẮT KẾT NỐI QUIZ KHỎI TẤT CẢ CÁC TASK LIÊN QUAN (Many-to-Many) ---
        // Lặp qua các Task đang liên kết với Quiz này

        // Sau khi tất cả các liên kết đã được xóa khỏi các Task, giờ an toàn để xóa Quiz.
        // Các Options cũng sẽ tự động bị xóa do CascadeType.ALL và orphanRemoval=true trong Quiz entity.
        quizRepository.delete(quizToDelete);
    }

//    @Transactional
//    public void linkQuizToTask(UUID quizId, Integer taskId) {
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));
//
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
//
//        task.getQuizzes().add(quiz);
//        taskRepository.save(task);
//    }

//    @Transactional
//    public void createTaskForQuiz(UUID quizId, UUID createdByUserId) {
//        Quiz quiz = quizRepository.findById(quizId)
//                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));
//
//        User creator = userRepository.findById(createdByUserId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + createdByUserId));
//
//        Task quizTask = new Task();
//        quizTask.setTypeId(1); // 1 cho Quiz Task
//        quizTask.setCreatedAt(LocalDateTime.now());
//        quizTask.setUpdatedAt(LocalDateTime.now());
//        quizTask.setCreatedByUser(creator);
//
//        Set<Quiz> quizzes = new HashSet<>();
//        quizzes.add(quiz);
//        quizTask.setQuizzes(quizzes);
//
//        taskRepository.save(quizTask);
//    }

    @Transactional
    public void importQuizzesFromExcel(MultipartFile file, UUID createdByUserId) {
        if (!QuizExcelService.hasExcelFormat(file)) {
            throw new IllegalArgumentException("Please upload an excel file!");
        }

        try {
            List<Quiz> quizzes = QuizExcelService.excelToQuizzes(file.getInputStream(), createdByUserId, userRepository);
            quizRepository.saveAll(quizzes);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }
}