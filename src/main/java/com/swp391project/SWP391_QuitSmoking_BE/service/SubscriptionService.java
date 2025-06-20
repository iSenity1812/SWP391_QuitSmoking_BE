package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Subscription;
import com.swp391project.SWP391_QuitSmoking_BE.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Optional<Subscription> getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id);
    }

    public Subscription createSubscription(Subscription subscription) {
        // Có thể thêm logic nghiệp vụ trước khi lưu
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubscription(Integer id, Subscription subscriptionDetails) {
        return subscriptionRepository.findById(id).map(subscription -> {
            // Cập nhật các trường của subscription từ subscriptionDetails
            subscription.setName(subscriptionDetails.getName());
            subscription.setDescription(subscriptionDetails.getDescription());
            subscription.setPrice(subscriptionDetails.getPrice());
            subscription.setDuration(subscriptionDetails.getDuration());
            // ... thêm các trường khác bạn muốn cập nhật

            return subscriptionRepository.save(subscription);
        }).orElseThrow(() -> new RuntimeException("Subscription not found with id " + id)); // Hoặc sử dụng exception tùy chỉnh
    }

    public void deleteSubscription(Integer id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new RuntimeException("Subscription not found with id " + id); // Hoặc sử dụng exception tùy chỉnh
        }
        subscriptionRepository.deleteById(id);
    }
}