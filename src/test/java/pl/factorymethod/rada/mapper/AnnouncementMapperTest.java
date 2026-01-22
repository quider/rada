package pl.factorymethod.rada.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pl.factorymethod.rada.dto.AnnouncementDto;
import pl.factorymethod.rada.model.Announcement;
import pl.factorymethod.rada.model.User;

@DisplayName("AnnouncementMapper Tests")
class AnnouncementMapperTest {

    private AnnouncementMapper announcementMapper;
    private User testUser;
    private Announcement testAnnouncement;

    @BeforeEach
    void setUp() {
        announcementMapper = new AnnouncementMapper();
        
        testUser = new User();
        testUser.setId(10L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        
        testAnnouncement = new Announcement();
        testAnnouncement.setId(20L);
        testAnnouncement.setUser(testUser);
        testAnnouncement.setSummary("Test Summary");
        testAnnouncement.setDescription("Test Description");
        testAnnouncement.setRead(false);
        testAnnouncement.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        testAnnouncement.setPublishedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    @Test
    @DisplayName("Should map announcement to DTO correctly")
    void shouldMapAnnouncementToDtoCorrectly() {
        // When
        AnnouncementDto dto = announcementMapper.toDto(testAnnouncement);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(testAnnouncement.getId());
        assertThat(dto.getUserId()).isEqualTo(testUser.getId());
        assertThat(dto.getUserName()).isEqualTo(testUser.getName());
        assertThat(dto.getSummary()).isEqualTo(testAnnouncement.getSummary());
        assertThat(dto.getDescription()).isEqualTo(testAnnouncement.getDescription());
        assertThat(dto.isRead()).isEqualTo(testAnnouncement.isRead());
        assertThat(dto.getCreatedAt()).isEqualTo(testAnnouncement.getCreatedAt());
        assertThat(dto.getPublishedAt()).isEqualTo(testAnnouncement.getPublishedAt());
    }

    @Test
    @DisplayName("Should map read announcement to DTO")
    void shouldMapReadAnnouncementToDto() {
        // Given
        testAnnouncement.setRead(true);

        // When
        AnnouncementDto dto = announcementMapper.toDto(testAnnouncement);

        // Then
        assertThat(dto.isRead()).isTrue();
    }

    @Test
    @DisplayName("Should return null when announcement is null")
    void shouldReturnNullWhenAnnouncementIsNull() {
        // When
        AnnouncementDto dto = announcementMapper.toDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should map announcement with all fields populated")
    void shouldMapAnnouncementWithAllFieldsPopulated() {
        // Given
        testAnnouncement.setSummary("Complete Summary");
        testAnnouncement.setDescription("Complete Description with more details");
        testAnnouncement.setRead(true);

        // When
        AnnouncementDto dto = announcementMapper.toDto(testAnnouncement);

        // Then
        assertThat(dto.getSummary()).isEqualTo("Complete Summary");
        assertThat(dto.getDescription()).isEqualTo("Complete Description with more details");
        assertThat(dto.isRead()).isTrue();
        assertThat(dto.getUserName()).isNotNull();
        assertThat(dto.getCreatedAt()).isNotNull();
        assertThat(dto.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should preserve ID values during mapping")
    void shouldPreserveIdValuesDuringMapping() {
        // Given
        Long expectedAnnouncementId = testAnnouncement.getId();
        Long expectedUserId = testUser.getId();

        // When
        AnnouncementDto dto = announcementMapper.toDto(testAnnouncement);

        // Then
        assertThat(dto.getId()).isEqualTo(expectedAnnouncementId);
        assertThat(dto.getUserId()).isEqualTo(expectedUserId);
    }

    @Test
    @DisplayName("Should preserve datetime values during mapping")
    void shouldPreserveDatetimeValuesDuringMapping() {
        // Given
        LocalDateTime expectedCreatedAt = testAnnouncement.getCreatedAt();
        LocalDateTime expectedPublishedAt = testAnnouncement.getPublishedAt();

        // When
        AnnouncementDto dto = announcementMapper.toDto(testAnnouncement);

        // Then
        assertThat(dto.getCreatedAt()).isEqualTo(expectedCreatedAt);
        assertThat(dto.getPublishedAt()).isEqualTo(expectedPublishedAt);
    }
}
