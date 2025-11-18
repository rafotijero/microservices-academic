package com.proyecto.academic.service;

import com.proyecto.academic.config.RabbitMQConfig;
import com.proyecto.academic.dto.EnrollmentMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void publishEnrollmentCreated(EnrollmentMessageDTO message) {
        try {
            log.info("Publicando mensaje en RabbitMQ: enrollmentId={}", message.getEnrollmentId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    message
            );
            log.info("Mensaje publicado exitosamente en RabbitMQ: enrollmentId={}", message.getEnrollmentId());
        } catch (Exception e) {
            log.error("Error al publicar mensaje en RabbitMQ para enrollmentId={}: {}",
                    message.getEnrollmentId(), e.getMessage());
        }
    }
}
