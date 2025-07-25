package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    // Lấy N Quiz ngẫu nhiên mà mỗi Quiz có đúng M options
    @Query(value = "SELECT q.* FROM Quiz q JOIN (SELECT o.QuizID, COUNT(o.OptionID) as option_count FROM Option o GROUP BY o.QuizID HAVING COUNT(o.OptionID) = :requiredOptionsCount) as qc ON q.QuizID = qc.QuizID ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Quiz> findNRandomQuizzesWithExactOptions(int limit, int requiredOptionsCount);

    // Tìm N quiz ngẫu nhiên, loại trừ các ID đã cho
    @Query(value = "SELECT q.* FROM Quiz q WHERE q.QuizID NOT IN :excludedIds ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Quiz> findNRandomQuizzesExcludingIds(int limit, Collection<UUID> excludedIds);

    // tìm N quiz ngẫu nhiên mà không cần cụ thể bao nhiêu options
    @Query(value = "SELECT q.* FROM Quiz q ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Quiz> findNRandomQuizzes(int limit);

    // Phương thức cũ findRandomQuizWithFourOptions có thể không cần thiết nếu dùng phương thức trên
    // @Query(value = "SELECT q.* FROM Quiz q JOIN Option o ON q.QuizID = o.QuizID GROUP BY q.QuizID HAVING COUNT(o.OptionID) = 4 ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    // Optional<Quiz> findRandomQuizWithFourOptions();
}