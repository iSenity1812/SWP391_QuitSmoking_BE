package com.swp391project.SWP391_QuitSmoking_BE.dto.member;

import java.util.UUID;

public class MemberResponseDTO {
    private UUID memberId;
    private String username; // Lấy từ User
    private String email;    // Lấy từ User
    private String profilePictureUrl; // Lấy từ User
    private int streak;
}
