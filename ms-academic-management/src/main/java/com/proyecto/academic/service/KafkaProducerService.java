package com.proyecto.academic.service;

import com.proyecto.academic.dto.EnrollmentEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC = "enrollment.events";

    private final KafkaTemplate<String, EnrollmentEventDTO> kafkaTemplate;

    public void publishEnrollmentCreated(EnrollmentEventDTO event) {
        String key = String.valueOf(event.getEnrollmentId());
        kafkaTemplate.send(TOPIC, key, event);
        log.info("Mensaje publicado en Kafka: enrollmentId={}", event.getEnrollmentId());
    }

    public void publishEnrollmentCancelled(EnrollmentEventDTO event) {
        String key = String.valueOf(event.getEnrollmentId());
        kafkaTemplate.send(TOPIC, key, event);
        log.info("Mensaje publicado en Kafka: enrollmentId={}", event.getEnrollmentId());
    }
}
