package com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MemberResponse {
    private UUID memberId;
    private int streak;
}
