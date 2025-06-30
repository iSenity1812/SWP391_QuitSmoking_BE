package com.swp391project.SWP391_QuitSmoking_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//khi người dùng cố gắng chỉnh sửa bản ghi theo dõi cơn thèm thuốc đã qua
@ResponseStatus(HttpStatus.FORBIDDEN) //không cho phép chỉnh sửa bản ghi đã qua
public class CravingTrackingEditForbiddenException extends RuntimeException {
    public CravingTrackingEditForbiddenException(String message) {
        super(message);
    }
}
