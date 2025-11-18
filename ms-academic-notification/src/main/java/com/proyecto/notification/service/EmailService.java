package com.proyecto.notification.service;

import com.proyecto.notification.dto.EnrollmentMessageDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final AtomicInteger emailsSent = new AtomicInteger(0);
    private final AtomicInteger emailsFailed = new AtomicInteger(0);

    public void sendEnrollmentConfirmation(EnrollmentMessageDTO enrollmentMessage) {
        try {
            log.info("Preparando email de confirmación para: {}", enrollmentMessage.getStudentEmail());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(enrollmentMessage.getStudentEmail());
            helper.setSubject("Confirmación de Matrícula - " + enrollmentMessage.getCourseName());
            helper.setFrom("noreply@universidad.edu");

            Context context = new Context();
            context.setVariable("studentName", enrollmentMessage.getStudentName());
            context.setVariable("enrollmentId", enrollmentMessage.getEnrollmentId());
            context.setVariable("courseCode", enrollmentMessage.getCourseCode());
            context.setVariable("courseName", enrollmentMessage.getCourseName());
            context.setVariable("courseCredits", enrollmentMessage.getCourseCredits());
            context.setVariable("enrollmentDate", enrollmentMessage.getEnrollmentDate());
            context.setVariable("status", enrollmentMessage.getStatus());

            String htmlContent = templateEngine.process("enrollment-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            emailsSent.incrementAndGet();
            log.info("Email enviado exitosamente a: {}", enrollmentMessage.getStudentEmail());

        } catch (MessagingException e) {
            emailsFailed.incrementAndGet();
            log.error("Error al enviar email a: {}", enrollmentMessage.getStudentEmail(), e);
            throw new RuntimeException("Error al enviar email", e);
        }
    }

    public int getEmailsSent() {
        return emailsSent.get();
    }

    public int getEmailsFailed() {
        return emailsFailed.get();
    }
}
