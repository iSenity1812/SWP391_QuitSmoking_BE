package com.swp391project.SWP391_QuitSmoking_BE.dto.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO để follow một user")
public class FollowRequestDTO {
    @NotNull(message = "User ID to follow cannot be null")
    @Schema(
        description = "ID của user muốn follow",
        example = "123e4567-e89b-12d3-a456-426614174000",
        required = true
    )
    private UUID followedUserId;
}
