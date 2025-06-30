package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Subscription;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
//    // Tìm kiếm tất cả Subscription mà một Member đã đăng ký
//    // Đường dẫn: Subscription (list memberSubscriptions) -> MemberSubscription (member object) -> Member (memberId field)
//    List<Subscription> findByMemberSubscriptions_Member_MemberId(UUID memberId);
//
//    // Kiểm tra xem có bất kỳ Subscription nào được liên kết với một Member cụ thể
//    // và có trạng thái đăng ký là ACTIVE trong MemberSubscription hay không.
//    // Đường dẫn: Subscription (list memberSubscriptions) -> MemberSubscription (member object) -> Member (memberId field)
//    // AND Subscription (list memberSubscriptions) -> MemberSubscription (subscriptionStatus field)
//    boolean existsByMemberSubscriptions_Member_MemberIdAndMemberSubscriptions_SubscriptionStatus(UUID memberId, SubscriptionStatus subscriptionStatus);
//
//    /**
//     * Tìm tất cả các Subscription được liên kết với MemberSubscription có trạng thái cụ thể.
//     * Ví dụ: Lấy tất cả các gói đăng ký đang có thành viên sử dụng (ACTIVE).
//     */
//    List<Subscription> findByMemberSubscriptions_SubscriptionStatus(SubscriptionStatus subscriptionStatus);
//
//    /**
//     * Tìm một Subscription theo tên, không phân biệt chữ hoa, chữ thường.
//     * Hữu ích để kiểm tra tên gói đăng ký đã tồn tại hay chưa.
//     */
//    Optional<Subscription> findByNameIgnoreCase(String name);
//
//    /**
//     * Kiểm tra xem một Subscription có tồn tại với tên cụ thể (không phân biệt chữ hoa/thường) hay không.
//     */
//    boolean existsByNameIgnoreCase(String name);
//
//    /**
//     * Tìm tất cả các Subscription có giá trong một khoảng cụ thể.
//     */
//    List<Subscription> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    Optional<Subscription> findByUser_UserIdAndIsActiveTrue(UUID userId);
    List<Subscription> findByUser(User user);
}
