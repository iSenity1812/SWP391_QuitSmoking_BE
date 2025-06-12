package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID; // Import UUID

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> { // OptionID là Integer
    // Tìm tất cả Options cho một Quiz cụ thể theo QuizID (UUID)
    List<Option> findByQuizQuizId(UUID quizId); // Truy vấn theo UUID QuizID
}