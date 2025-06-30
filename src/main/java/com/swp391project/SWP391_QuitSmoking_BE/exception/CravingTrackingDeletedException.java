package com.swp391project.SWP391_QuitSmoking_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Bản ghi theo dõi cơn thèm thuốc đã bị xóa do giá trị cần thiết set thành 0
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CravingTrackingDeletedException extends RuntimeException {
    public CravingTrackingDeletedException(String message) {
        super(message);
    }
}