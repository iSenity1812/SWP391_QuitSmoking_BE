package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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

        try {
            Context context = new Context();

            context.setVariable("name", "Huy");
            String html = templateEngine.process("emailTemplate", context); // Assuming you have an email template named "emailTemplate"

            // Create a simple email message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.setSubject(emailDetail.getSubject());
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            // Setting up necessary details
            mimeMessage.setText(html, "UTF-8", "html");
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);

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
