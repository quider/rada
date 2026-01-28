package pl.factorymethod.rada.targets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.factorymethod.rada.model.Target;
import pl.factorymethod.rada.model.TargetStudent;
import pl.factorymethod.rada.shared.events.DomainEventPublisher;
import pl.factorymethod.rada.targets.event.TargetContributionCollectionOpenedEvent;
import pl.factorymethod.rada.targets.repository.StudentRepository;
import pl.factorymethod.rada.targets.repository.TargetRepository;
import pl.factorymethod.rada.targets.repository.TargetStudentRepository;

class TargetServiceTest {

    @Mock
    private TargetRepository targetRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TargetStudentRepository targetStudentRepository;
    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private TargetService targetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void opensCollectionAndFreezesFees() {
        Target target = new Target();
        target.setId(1L);
        target.setPublicId(UUID.randomUUID());
        target.setEstimatedValue(BigDecimal.valueOf(100));

        TargetStudent ts1 = new TargetStudent();
        TargetStudent ts2 = new TargetStudent();

        when(targetRepository.findByPublicId(target.getPublicId())).thenReturn(Optional.of(target));
        when(targetStudentRepository.findByTarget(target)).thenReturn(List.of(ts1, ts2));

        targetService.openContributionCollection(target.getPublicId().toString());

        ArgumentCaptor<List<TargetStudent>> captor = ArgumentCaptor.forClass(List.class);
        verify(targetStudentRepository).saveAll(captor.capture());
        List<TargetStudent> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved)
            .allSatisfy(ts -> {
                assertThat(ts.getFeeAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
                assertThat(ts.getFeeCalculatedAt()).isNotNull();
            });

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(domainEventPublisher, times(1)).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(TargetContributionCollectionOpenedEvent.class);
    }

    @Test
    void throwsWhenNoStudentsAssigned() {
        Target target = new Target();
        target.setPublicId(UUID.randomUUID());
        target.setEstimatedValue(BigDecimal.valueOf(50));

        when(targetRepository.findByPublicId(target.getPublicId())).thenReturn(Optional.of(target));
        when(targetStudentRepository.findByTarget(target)).thenReturn(List.of());

        assertThatThrownBy(() -> targetService.openContributionCollection(target.getPublicId().toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No students assigned");
    }
}
