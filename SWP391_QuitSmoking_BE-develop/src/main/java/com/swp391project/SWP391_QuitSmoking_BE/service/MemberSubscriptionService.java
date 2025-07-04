//package com.swp391project.SWP391_QuitSmoking_BE.service;
//
//import com.swp391project.SWP391_QuitSmoking_BE.dto.premiumMember.MemberSubscriptionResponse;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberSubscription;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberSubscriptionRepository;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.SubscriptionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
////@Service
////public class MemberSubscriptionService {
////    private final MemberSubscriptionRepository memberSubscriptionRepository;
////    private final MemberRepository memberRepository;
////    private final SubscriptionRepository subscriptionRepository;
////
////    @Autowired
////    public MemberSubscriptionService(Q
////            MemberSubscriptionRepository memberSubscriptionRepository,
////            MemberRepository memberRepository,
////            SubscriptionRepository subscriptionRepository
////    ) {
////        this.memberSubscriptionRepository = memberSubscriptionRepository;
////        this.memberRepository = memberRepository;
////        this.subscriptionRepository = subscriptionRepository;
////    }
////
////    private MemberSubscriptionResponse convertToResponseDto(MemberSubscription memberSubscription) {
////        MemberSubscriptionResponse response = new MemberSubscriptionResponse();
////        response.setMemberSubscriptionId(memberSubscription.getMemberSubscriptionId());
////        response.setStartDate(memberSubscription.getStartDate());
////        response.setEndDate(memberSubscription.getEndDate());
////        response.setSubscriptionStatus(memberSubscription.getSubscriptionStatus());
////        response.setPurchasedAt(memberSubscription.getPurchasedAt());
////
////        if (memberSubscription.getMember() != null) {
////            response.setMemberId(memberSubscription.getMember().getMemberId());
////        }
////        if (memberSubscription.getSubscription() != null) {
////            response.setSubscriptionName(memberSubscription.getSubscription().getName());
////        }
////        return response;
////    }
////}
