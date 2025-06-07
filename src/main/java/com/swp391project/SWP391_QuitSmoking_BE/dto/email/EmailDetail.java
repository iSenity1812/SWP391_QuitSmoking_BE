package com.swp391project.SWP391_QuitSmoking_BE.dto.email;

import lombok.Data;

@Data
public class EmailDetail {
    private String recipient; // Recipient's email address
    private String subject; // Subject of the email
    private String body; // Body content of the email
    private String attachment; // Optional attachment file path or URL

    public EmailDetail(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    public EmailDetail(String recipient, String subject, String body, String attachment) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.attachment = attachment;
    }
}
