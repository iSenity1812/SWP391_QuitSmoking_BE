package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EmailDetail emailDetail) {
        // Implement email sending logic here
        // This could involve using an SMTP server, a third-party email service, etc.
        // For example, you might use JavaMailSender or an external service like SendGrid or Amazon SES.
//        if (javaMailSender instanceof JavaMailSenderImpl) {
//            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) javaMailSender;
//            System.out.println("--- DEBUGGING JAVAMAILSENDERIMPL PROPERTIES ---");
//            System.out.println("  JavaMailSenderImpl Username: " + mailSenderImpl.getUsername());
//            String passwordFromSender = mailSenderImpl.getPassword();
//            String maskedPasswordFromSender = (passwordFromSender != null && passwordFromSender.length() > 4) ?
//                    passwordFromSender.substring(0, 4) + "..." :
//                    (passwordFromSender != null ? passwordFromSender : "null");
//            System.out.println("  JavaMailSenderImpl Password (masked): " + maskedPasswordFromSender + " (length: " + (passwordFromSender != null ? passwordFromSender.length() : 0) + ")");
//
//            // Bạn cũng có thể in ra toàn bộ các thuộc tính mail nếu muốn
//            // System.out.println("  JavaMailSenderImpl Raw JavaMail Properties: " + mailSenderImpl.getJavaMailProperties());
//            System.out.println("---------------------------------------------");
//        } else {
//            System.out.println("--- JAVAMAILSENDER KHÔNG PHẢI LÀ JAVAMAILSENDERIMPL, KHÔNG THỂ KIỂM TRA TRỰC TIẾP ---");
//        }
        try {
            Context context = new Context();
            // Set variables for the email template
            if (emailDetail.getTemplateVariables() != null) {
                emailDetail.getTemplateVariables().forEach(context::setVariable);
            }

            System.out.println("Đang gửi email đến: " + emailDetail.getRecipient());

//            context.setVariable("name", ); // Lấy tên người nhận từ emailDetail nếu có
            String html;
            if (emailDetail.getTemplateName() != null && !emailDetail.getTemplateName().isEmpty()) {
                html = templateEngine.process(emailDetail.getTemplateName(), context);// Assuming you have an email template named "emailTemplate"
            } else if (emailDetail.getBody() != null && !emailDetail.getBody().isEmpty()) {
                html = emailDetail.getBody(); // Use the body directly if no template is provided
            } else {
                throw new IllegalArgumentException("No template or body provided for email.");
            }

            // Create a simple email message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            // Setting up necessary details
            mimeMessageHelper.setFrom("anonymous@gmail.com"); // Replace with your sender email
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());

            System.out.println("Setup thanh công email: " + emailDetail.getSubject() + " đến " + emailDetail.getRecipient());
            javaMailSender.send(mimeMessage);
            System.out.println("Email gửi thành công");

        } catch (MessagingException e) {
//            logger.error("Lỗi khi tạo hoặc gửi tin nhắn MIME: {}", e.getMessage(), e);
            // Bạn có thể ném lại một exception tùy chỉnh ở đây
            throw new RuntimeException("Không thể gửi email HTML: " + e.getMessage(), e);
        } catch (Exception e) { // Bắt các lỗi khác có thể xảy ra
//            logger.error("Lỗi không mong muốn khi gửi email: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi không mong muốn khi gửi email: " + e.getMessage(), e);
        }
    }
}
