package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.CoachProfile;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.CoachSpecialty;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CoachService {
    private final CoachRepository coachRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private static final Logger log = LoggerFactory.getLogger(CoachService.class);

    @Autowired
    public CoachService(
            CoachRepository coachRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.coachRepository = coachRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private CoachProfile convertToResponseDto(Coach coach) {
        CoachProfile response = new CoachProfile();
        try {
            response.setCoachBio(coach.getCoachBio());
            response.setFullName(coach.getFullName());
            response.setRating(coach.getRating());
            response.setCoachId(coach.getCoachId());
            if (coach.getUser() != null) {
                response.setUsername(coach.getUser().getUsername());
            } else {
                log.error("Coach {} does not have a linked user!", coach.getCoachId());
                response.setUsername("(Không có user)");
            }
            response.setSpecialties(
                coach.getSpecialties() != null
                    ? String.join(", ", coach.getSpecialties().stream().map(Enum::name).toList())
                    : "Chưa có chuyên môn"
            );
            response.setActive(true);
        } catch (Exception e) {
            log.error("Error mapping CoachProfile for coach {}: {}", coach.getCoachId(), e.getMessage(), e);
            throw new RuntimeException("Lỗi khi trả về thông tin huấn luyện viên: " + e.getMessage());
        }
        return response;
    }

    //được gọi sau khi một User vừa mới được đăng ký
    @Transactional
    public void createCoachForUser(User user, String fullName, String coachBio) {

        Coach coach = new Coach();
        coach.setUser(user); // Coach trỏ đến User
        coach.setCoachBio(coachBio);
        coach.setFullName(fullName);
        coach.setRating(BigDecimal.valueOf(0.0));
        user.setCoach(coach); // User trỏ đến Coach
        coachRepository.save(coach);
    }

    @Transactional
    public List<Coach> getAllCoachesWithUserDetails() {
        return coachRepository.findAllWithUser();
    }


    @Transactional
    public CoachSimpleResponseDTO updateCoachSpecialties(UUID coachId, Set<CoachSpecialty> newSpecialties) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Huấn luyện viên với ID: " + coachId));

        // Xóa tất cả chuyên môn cũ và thêm các chuyên môn mới
        // Sử dụng .clear() và .addAll() để đảm bảo JPA nhận biết sự thay đổi trong Collection
        coach.getSpecialties().clear();
        if (newSpecialties != null) {
            coach.getSpecialties().addAll(newSpecialties);
        }

        Coach updatedCoach = coachRepository.save(coach);
        return modelMapper.map(updatedCoach, CoachSimpleResponseDTO.class);
    }

    @Transactional
    public CoachProfile getCoachProfile(UUID coachId)
    {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Huấn luyện viên với ID: " + coachId));
        return convertToResponseDto(coach);
    }
}
