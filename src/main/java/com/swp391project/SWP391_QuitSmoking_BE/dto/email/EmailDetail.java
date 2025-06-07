package com.swp391project.SWP391_QuitSmoking_BE.dto.email;

import lombok.Data;

import java.util.Map;

@Data
public class EmailDetail {
    private String recipient; // Recipient's email address
    private String subject; // Subject of the email
    private String body; // Body content of the email
    private String attachment; // Optional attachment file path or URL

    private String templateName; // Optional template name for email content
    private Map<String, Object> templateVariables; // Variables for the email template

    public EmailDetail(String recipient, String subject, String body, String templateName, Map<String, Object> templateVariables) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.templateName = templateName;
        this.templateVariables = templateVariables;
    }

    public EmailDetail(String recipient, String subject, String body, String attachment, String templateName, Map<String, Object> templateVariables) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.attachment = attachment;
        this.templateName = templateName;
        this.templateVariables = templateVariables;
    }
}
