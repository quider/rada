package pl.factorymethod.rada.outbox;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public void record(Object event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(event.getClass().getName());
        outboxEvent.setStatus(OutboxEventStatus.PENDING.name());
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setPayload(serialize(event));

        repository.save(outboxEvent);
        log.info("Outbox event stored: type={}", outboxEvent.getEventType());
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event " + event.getClass().getName(), e);
        }
    }
}
