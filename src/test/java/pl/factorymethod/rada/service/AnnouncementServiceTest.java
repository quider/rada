package pl.factorymethod.rada.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.factorymethod.rada.model.Announcement;
import pl.factorymethod.rada.model.User;
import pl.factorymethod.rada.repository.AnnouncementRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnnouncementService Tests")
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @InjectMocks
    private AnnouncementService announcementService;

    private Long userId;
    private Long announcementId;
    private User testUser;
    private Announcement testAnnouncement;

    @BeforeEach
    void setUp() {
        userId = 1L;
        announcementId = 2L;
        
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        
        testAnnouncement = new Announcement();
        testAnnouncement.setId(announcementId);
        testAnnouncement.setUser(testUser);
        testAnnouncement.setSummary("Test Summary");
        testAnnouncement.setDescription("Test Description");
        testAnnouncement.setRead(false);
        testAnnouncement.setCreatedAt(LocalDateTime.now());
        testAnnouncement.setPublishedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get all announcements by user ID")
    void shouldGetAnnouncementsByUserId() {
        // Given
        Announcement announcement1 = createAnnouncement("Summary 1", false);
        Announcement announcement2 = createAnnouncement("Summary 2", true);
        List<Announcement> expectedAnnouncements = Arrays.asList(announcement1, announcement2);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(announcementRepository.findByUserId(userId, pageable))
            .thenReturn(new PageImpl<>(expectedAnnouncements));

        // When
        List<Announcement> result = announcementService.getAnnouncementsByUserId(userId, pageable);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(announcement1, announcement2);
        verify(announcementRepository).findByUserId(userId, pageable);
    }

    @Test
    @DisplayName("Should get unread announcements by user ID")
    void shouldGetUnreadAnnouncementsByUserId() {
        // Given
        Announcement unreadAnnouncement = createAnnouncement("Unread", false);
        List<Announcement> expectedAnnouncements = Arrays.asList(unreadAnnouncement);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(announcementRepository.findUnreadByUserId(userId, pageable))
            .thenReturn(new PageImpl<>(expectedAnnouncements));

        // When
        List<Announcement> result = announcementService.getUnreadAnnouncementsByUserId(userId, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isRead()).isFalse();
        verify(announcementRepository).findUnreadByUserId(userId, pageable);
    }

    @Test
    @DisplayName("Should get read announcements by user ID")
    void shouldGetReadAnnouncementsByUserId() {
        // Given
        Announcement readAnnouncement = createAnnouncement("Read", true);
        List<Announcement> expectedAnnouncements = Arrays.asList(readAnnouncement);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(announcementRepository.findReadByUserId(userId, pageable))
            .thenReturn(new PageImpl<>(expectedAnnouncements));

        // When
        List<Announcement> result = announcementService.getReadAnnouncementsByUserId(userId, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isRead()).isTrue();
        verify(announcementRepository).findReadByUserId(userId, pageable);
    }

    @Test
    @DisplayName("Should mark announcement as read")
    void shouldMarkAnnouncementAsRead() {
        // Given
        testAnnouncement.setRead(false);
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(testAnnouncement));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(testAnnouncement);

        // When
        Announcement result = announcementService.markAsRead(announcementId);

        // Then
        assertThat(result.isRead()).isTrue();
        verify(announcementRepository).findById(announcementId);
        verify(announcementRepository).save(testAnnouncement);
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent announcement as read")
    void shouldThrowExceptionWhenMarkingNonExistentAnnouncementAsRead() {
        // Given
        Long nonExistentId = 999L;
        when(announcementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> announcementService.markAsRead(nonExistentId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Announcement not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("Should get announcement by ID")
    void shouldGetAnnouncementById() {
        // Given
        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(testAnnouncement));

        // When
        Announcement result = announcementService.getAnnouncementById(announcementId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(announcementId);
        assertThat(result.getSummary()).isEqualTo("Test Summary");
        verify(announcementRepository).findById(announcementId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent announcement")
    void shouldThrowExceptionWhenGettingNonExistentAnnouncement() {
        // Given
        Long nonExistentId = 999L;
        when(announcementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> announcementService.getAnnouncementById(nonExistentId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Announcement not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("Should return empty list when user has no announcements")
    void shouldReturnEmptyListWhenUserHasNoAnnouncements() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        when(announcementRepository.findByUserId(userId, pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        // When
        List<Announcement> result = announcementService.getAnnouncementsByUserId(userId, pageable);

        // Then
        assertThat(result).isEmpty();
        verify(announcementRepository).findByUserId(userId, pageable);
    }

    private Announcement createAnnouncement(String summary, boolean read) {
        Announcement announcement = new Announcement();
        announcement.setId(3L);
        announcement.setUser(testUser);
        announcement.setSummary(summary);
        announcement.setDescription("Description");
        announcement.setRead(read);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setPublishedAt(LocalDateTime.now());
        return announcement;
    }
}
