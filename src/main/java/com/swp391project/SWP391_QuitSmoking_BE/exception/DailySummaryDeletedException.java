package com.swp391project.SWP391_QuitSmoking_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Bản ghi tóm tắt hàng ngày đã bị xóa do giá trị cần thiết set thành 0
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DailySummaryDeletedException extends RuntimeException {
    public DailySummaryDeletedException(String message) {
        super(message);
    }
}

