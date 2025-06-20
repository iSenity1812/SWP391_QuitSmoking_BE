package com.swp391project.SWP391_QuitSmoking_BE.exception;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {
        super(message);
    }
}
