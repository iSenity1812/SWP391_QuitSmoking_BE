package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.profile.PublicProfileDTO;

import java.util.UUID;

public interface ProfileService {
    Object getMyProfile(UUID userId);
    PublicProfileDTO getPublicProfile(UUID userId);
}
