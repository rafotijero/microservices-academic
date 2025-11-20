package com.proyecto.audit.listener;

import com.proyecto.audit.dto.EnrollmentEventDTO;
import com.proyecto.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnrollmentEventListener {

    private final AuditService auditService;

    @KafkaListener(topics = "enrollment.events", groupId = "audit-service-group")
    public void handleEnrollmentEvent(EnrollmentEventDTO event) {
        log.info("Received enrollment event: {} - Type: {}", event.getEventId(), event.getEventType());

        try {
            if (event.getEventId() == null || event.getEventType() == null) {
                log.error("Invalid event received - missing required fields: {}", event);
                return;
            }

            var savedEvent = auditService.saveEvent(event);
            if (savedEvent != null) {
                log.info("Event processed and saved successfully: {}", event.getEventId());
            } else {
                log.info("Event already exists, skipping: {}", event.getEventId());
            }
        } catch (Exception e) {
            log.error("Error processing event {}: {}", event.getEventId(), e.getMessage(), e);
        }
    }
}
