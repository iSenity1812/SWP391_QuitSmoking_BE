package com.swp391project.SWP391_QuitSmoking_BE.services;

import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

}
