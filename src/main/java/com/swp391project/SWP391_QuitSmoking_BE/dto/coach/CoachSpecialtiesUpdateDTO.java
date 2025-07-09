package com.swp391project.SWP391_QuitSmoking_BE.dto.coach;

import com.swp391project.SWP391_QuitSmoking_BE.enums.CoachSpecialty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class CoachSpecialtiesUpdateDTO {
    @NotNull(message = "Danh sách chuyên môn không được để trống (có thể là rỗng, nhưng không null)")
    private Set<CoachSpecialty> specialties;
}
