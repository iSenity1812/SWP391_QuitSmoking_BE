package com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSearchDTO {
    private UUID memberId;
    private String username;
    private String email;
}
