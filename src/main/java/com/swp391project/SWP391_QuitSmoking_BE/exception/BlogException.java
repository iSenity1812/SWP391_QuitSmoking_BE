package com.swp391project.SWP391_QuitSmoking_BE.exception;

public class BlogException extends RuntimeException {

    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }
}