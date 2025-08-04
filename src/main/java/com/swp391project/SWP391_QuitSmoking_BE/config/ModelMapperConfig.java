package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.dto.blog.BlogResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
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
        
        // Cấu hình mapping cụ thể cho Blog -> BlogResponseDTO
        modelMapper.createTypeMap(Blog.class, BlogResponseDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Blog::getBlogId, BlogResponseDTO::setBlogId);
                    mapper.map(Blog::getTitle, BlogResponseDTO::setTitle);
                    mapper.map(Blog::getContent, BlogResponseDTO::setContent);
                    mapper.map(Blog::getImageUrl, BlogResponseDTO::setImageUrl);
                    mapper.map(Blog::getStatus, BlogResponseDTO::setStatus);
                    mapper.map(Blog::getCreatedAt, BlogResponseDTO::setCreatedAt);
                    mapper.map(Blog::getLastUpdated, BlogResponseDTO::setLastUpdated);
                    mapper.map(Blog::getApprovedAt, BlogResponseDTO::setApprovedAt);
                });
        
        return modelMapper;
    }

}