package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Cấu hình chung cho ModelMapper
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT) // Khuyến nghị sử dụng STRICT
                .setSkipNullEnabled(true); // Bỏ qua các trường null khi ánh xạ, hữu ích cho update DTOs
        // Các cấu hình ánh xạ cụ thể sẽ được thêm ở đây hoặc trong các Configurer riêng biệt
        return modelMapper;
    }

}