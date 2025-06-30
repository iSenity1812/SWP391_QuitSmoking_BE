package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.option.OptionResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.QuizAttemptResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.QuizResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.SubmitQuizAttemptRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.task.TaskResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.tip.TipResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository;
    private final TipRepository tipRepository;
    private final UserRepository userRepository;

    private static final int TYPE_ID_QUIZ_TASK = 1;
    private static final int TYPE_ID_TIP_TASK = 2;
    private static final int REQUIRED_QUIZ_OPTIONS = 4;
    private static final int NUMBER_OF_QUIZZES_IN_TASK = 5;

    @Autowired
    public TaskService(TaskRepository taskRepository, QuizRepository quizRepository,
                       OptionRepository optionRepository, TipRepository tipRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.quizRepository = quizRepository;
        this.optionRepository = optionRepository;
        this.tipRepository = tipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponseDTO generateRandomCravingTask(UUID memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        Random random = new Random();
        boolean isQuizTaskType = random.nextBoolean();

        Task task = new Task();
        task.setCreatedAt(LocalDateTime.now());
        task.setCreatedByUser(member);

        if (isQuizTaskType) {
            List<Quiz> selectedQuizzes = quizRepository.findNRandomQuizzesWithExactOptions(NUMBER_OF_QUIZZES_IN_TASK, REQUIRED_QUIZ_OPTIONS);

            if (selectedQuizzes.size() < NUMBER_OF_QUIZZES_IN_TASK) {
                throw new ResourceNotFoundException("Không tìm thấy đủ " + NUMBER_OF_QUIZZES_IN_TASK + " Quiz, mỗi Quiz có " + REQUIRED_QUIZ_OPTIONS + " lựa chọn. Vui lòng thêm Quiz phù hợp vào hệ thống.");
            }

            task.setTypeId(TYPE_ID_QUIZ_TASK);
            task.setQuizzes(new HashSet<>(selectedQuizzes));

            Task savedTask = taskRepository.save(task);

            return convertToTaskResponseDTO(savedTask);

        } else {
            Tip randomTip = tipRepository.findRandomTip()
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tip nào. Vui lòng thêm Tip vào hệ thống."));

            task.setTypeId(TYPE_ID_TIP_TASK);
            task.setTips(Set.of(randomTip));

            Task savedTask = taskRepository.save(task);

            return convertToTaskResponseDTO(savedTask);
        }
    }

    private TaskResponseDTO convertToTaskResponseDTO(Task task) {
        List<QuizResponseDTO> quizResponses = null;
        TipResponseDTO tipResponse = null;

        if (task.getTypeId() == TYPE_ID_QUIZ_TASK && task.getQuizzes() != null && !task.getQuizzes().isEmpty()) {
            quizResponses = task.getQuizzes().stream()
                    .map(quiz -> {
                        List<Option> options = optionRepository.findByQuizQuizId(quiz.getQuizId());
                        List<OptionResponseDTO> optionResponses = options.stream()
                                .map(option -> new OptionResponseDTO(option.getOptionId(), option.getContent()))
                                .collect(Collectors.toList());
                        return new QuizResponseDTO(quiz.getQuizId(), quiz.getTitle(), quiz.getDescription(), quiz.getScorePossible(), optionResponses);
                    })
                    .collect(Collectors.toList());
        } else if (task.getTypeId() == TYPE_ID_TIP_TASK && task.getTips() != null && !task.getTips().isEmpty()) {
            Tip relatedTip = task.getTips().iterator().next();
            tipResponse = new TipResponseDTO(relatedTip.getTipId(), relatedTip.getContent());
        }

        return new TaskResponseDTO(
                task.getTaskId(),
                task.getCreatedAt(),
                task.getTypeId(),
                quizResponses,
                tipResponse
        );
    }


    @Transactional
    public QuizAttemptResponseDTO submitQuizAttempt(UUID memberId, SubmitQuizAttemptRequestDTO request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + request.getTaskId()));

        userRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        if (task.getTypeId() != TYPE_ID_QUIZ_TASK) {
            throw new IllegalArgumentException("Task này không phải là một Quiz Task. Không thể nộp bài Quiz.");
        }

        Quiz quizBeingAttempted = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + request.getQuizId()));

        boolean quizBelongsToTask = task.getQuizzes().stream()
                .anyMatch(q -> q.getQuizId().equals(quizBeingAttempted.getQuizId()));
        if (!quizBelongsToTask) {
            throw new IllegalArgumentException("Quiz với ID: " + request.getQuizId() + " không thuộc Task có ID: " + request.getTaskId());
        }

        List<Option> optionsForQuiz = optionRepository.findByQuizQuizId(quizBeingAttempted.getQuizId());
        if (optionsForQuiz.size() != REQUIRED_QUIZ_OPTIONS) {
            throw new IllegalArgumentException("Quiz với ID: " + request.getQuizId() + " không có đủ " + REQUIRED_QUIZ_OPTIONS + " lựa chọn hợp lệ.");
        }

        int totalScore = 0;
        int correctAnswersCount = 0;
        Map<UUID, Boolean> quizResults = new HashMap<>();
        String responseMessage; // Thêm biến để lưu thông báo kết quả

        Map<Integer, Option> optionMap = optionsForQuiz.stream()
                .collect(Collectors.toMap(Option::getOptionId, o -> o));

        if (request.getUserAnswers().size() != 1) {
            throw new IllegalArgumentException("Chỉ được gửi 1 câu trả lời cho mỗi Quiz.");
        }

        SubmitQuizAttemptRequestDTO.QuizAttemptDetail detail = request.getUserAnswers().get(0);
        Option selectedOption = optionMap.get(detail.getSelectedOptionId());

        if (selectedOption != null && selectedOption.getIsCorrect()) {
            correctAnswersCount = 1;
            totalScore = quizBeingAttempted.getScorePossible(); // Sẽ là 1
            quizResults.put(quizBeingAttempted.getQuizId(), true);
            responseMessage = "Chúc mừng! Đáp án bạn chọn là ĐÚNG."; // Thông báo khi đúng
        } else {
            correctAnswersCount = 0;
            totalScore = 0;
            quizResults.put(quizBeingAttempted.getQuizId(), false);
            responseMessage = "Rất tiếc! Đáp án bạn chọn là SAI."; // Thông báo khi sai
        }

        // Không có logic lưu trữ tổng điểm hay trạng thái hoàn thành Task ở đây,
        // chỉ trả về kết quả ngay lập tức cho câu trả lời.

        return new QuizAttemptResponseDTO(
                task.getTaskId(),
                totalScore,
                correctAnswersCount,
                quizBeingAttempted.getScorePossible(), // Tổng số câu hỏi của Quiz này (luôn là 1)
                quizResults,
                responseMessage // Gán thông báo kết quả vào đây
        );
    }
}