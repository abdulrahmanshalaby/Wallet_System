package com.wallet.auth_service.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailVerification(String to, String token) {
        String subject = "Verify your email";
    String verificationLink = "http://localhost:5173/verify?token=" + token;
        String body = "Hi,\n\nPlease verify your email using the link below:\n" 
                    + verificationLink + "\n\nThank you!";
        sendEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Reset your password";
        String resetLink = "http://localhost:5173" + token;
        String body = "Hello,\n\nClick below to reset your password:\n" 
                    + resetLink + "\n\nIf you didn’t request this, ignore this email.";
        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
