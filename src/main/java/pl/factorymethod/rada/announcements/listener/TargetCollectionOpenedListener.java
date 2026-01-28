package pl.factorymethod.rada.announcements.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.targets.event.TargetContributionCollectionOpenedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class TargetCollectionOpenedListener {

    @EventListener
    public void handle(TargetContributionCollectionOpenedEvent event) {
        // For now just log; dedicated announcement creation stays decoupled.
        log.info("[EventListener] Collection opened for target {} | feePerStudent={} | students={}",
                event.targetPublicId(), event.feePerStudent(), event.studentCount());
    }
}
