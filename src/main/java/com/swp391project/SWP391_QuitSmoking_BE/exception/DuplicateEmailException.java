package com.swp391project.SWP391_QuitSmoking_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for when an email already exists during registration.
 * This will return an HTTP 409 Conflict status.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Annotation này sẽ tự động thiết lập HTTP status code là 409 (Conflict) khi exception này được ném ra.
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}