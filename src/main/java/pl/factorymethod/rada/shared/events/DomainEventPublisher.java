package pl.factorymethod.rada.shared.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.factorymethod.rada.outbox.OutboxService;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final OutboxService outboxService;

    @Override
    public void publish(Object event) {
        outboxService.record(event);
        eventPublisher.publishEvent(event);
    }
}
