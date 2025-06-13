package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TransactionMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionMethodRepository extends JpaRepository<TransactionMethod, Integer> {
    Optional<TransactionMethod> findByMethodName(String methodName);
}
