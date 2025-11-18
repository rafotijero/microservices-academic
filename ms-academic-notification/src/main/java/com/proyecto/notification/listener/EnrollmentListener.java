package com.proyecto.notification.listener;

import com.proyecto.notification.dto.EnrollmentMessageDTO;
import com.proyecto.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableRetry
public class EnrollmentListener {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void handleEnrollmentMessage(EnrollmentMessageDTO message) {
        log.info("Mensaje recibido de RabbitMQ: Matrícula ID {} para estudiante {}",
                message.getEnrollmentId(), message.getStudentName());

        try {
            emailService.sendEnrollmentConfirmation(message);
            log.info("Notificación procesada exitosamente para matrícula ID: {}", message.getEnrollmentId());
        } catch (Exception e) {
            log.error("Error al procesar mensaje de matrícula ID: {}", message.getEnrollmentId(), e);
            throw e;
        }
    }
}
