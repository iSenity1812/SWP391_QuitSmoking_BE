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
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubscription(Integer id, Subscription subscriptionDetails) {
        return subscriptionRepository.findById(id).map(subscription -> {
            subscription.setName(subscriptionDetails.getName());
            subscription.setPrice(subscriptionDetails.getPrice());
            subscription.setDuration(subscriptionDetails.getDuration());
            subscription.setDurationType(subscriptionDetails.getDurationType());
            subscription.setDescription(subscriptionDetails.getDescription());
            return subscriptionRepository.save(subscription);
        }).orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    public void deleteSubscription(Integer id) {
        subscriptionRepository.deleteById(id);
    }
}