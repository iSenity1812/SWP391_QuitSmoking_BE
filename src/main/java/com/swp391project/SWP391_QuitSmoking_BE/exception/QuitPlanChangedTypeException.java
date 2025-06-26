package com.swp391project.SWP391_QuitSmoking_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QuitPlanChangedTypeException extends RuntimeException {
    public QuitPlanChangedTypeException(String message) {
        super(message);
    }
}
