package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email cannot be blank")
    @Size(min = 3, max = 100, message = "Identifier must be between 3 and 100 characters")
    private String identifier; // Can be username or email

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 18, message = "Password must be between 6 and 18 characters")
    private String password;

}
