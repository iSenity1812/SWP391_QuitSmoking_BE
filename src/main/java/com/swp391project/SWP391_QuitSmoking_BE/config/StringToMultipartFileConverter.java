package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StringToMultipartFileConverter implements Converter<String, MultipartFile> {

    @Override
    public MultipartFile convert(String source) {
        // Nếu là empty string hoặc null, trả về null
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        // Nếu không phải empty string, để Spring xử lý conversion error
        throw new IllegalArgumentException("Cannot convert non-empty string to MultipartFile");
    }
}
