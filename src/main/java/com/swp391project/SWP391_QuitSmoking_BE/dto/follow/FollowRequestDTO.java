package com.swp391project.SWP391_QuitSmoking_BE.dto.follow;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDTO {
    @NotNull(message = "User ID to follow cannot be null")
    private UUID followedUserId;
}
