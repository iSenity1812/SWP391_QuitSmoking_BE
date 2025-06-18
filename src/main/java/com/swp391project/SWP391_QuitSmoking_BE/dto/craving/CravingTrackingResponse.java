package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CravingTrackingResponse {
    private Integer cravingTrackingId;
    private LocalDateTime trackTime;
    private Integer smokedCount;
    private Integer cravingsCount;
    private Set<Situation> situations;
    private Set<WithWhom> withWhoms;
}
