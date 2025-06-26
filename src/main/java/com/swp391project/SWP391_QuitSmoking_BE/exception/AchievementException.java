package com.swp391project.SWP391_QuitSmoking_BE.exception;

public class AchievementException extends RuntimeException {

    public AchievementException(String message) {
        super(message);
    }

    public AchievementException(String message, Throwable cause) {
        super(message, cause);
    }
}