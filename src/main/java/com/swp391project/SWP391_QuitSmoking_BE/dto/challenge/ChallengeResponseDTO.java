package com.swp391project.SWP391_QuitSmoking_BE.dto.challenge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeResponseDTO {
    private Integer challengeID;
    private UUID memberID;
    private String challengeName;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime endDate;
    private BigDecimal targetValue;
    private String unit;
    private String status;

    public ChallengeResponseDTO(Challenge challenge) {
        this.challengeID = challenge.getChallengeID();
        this.memberID = challenge.getMemberID();
        this.challengeName = challenge.getChallengeName();
        this.description = challenge.getDescription();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.targetValue = challenge.getTargetValue();
        this.unit = challenge.getUnit();
        this.status = challenge.getStatus();
    }
}
