package pl.factorymethod.rada.announcements.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pl.factorymethod.rada.announcements.dto.AnnouncementDto;
import pl.factorymethod.rada.announcements.mapper.AnnouncementMapper;
import pl.factorymethod.rada.announcements.service.AnnouncementService;
import pl.factorymethod.rada.model.Announcement;
import pl.factorymethod.rada.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=false"
})
@DisplayName("AnnouncementsController Tests")
class AnnouncementsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private AnnouncementService announcementService;

    @MockitoBean
    private AnnouncementMapper announcementMapper;

    private Long userId;
    private Long announcementId;
    private User testUser;
    private Announcement testAnnouncement;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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
    @DisplayName("Should get all announcements for user")
    void shouldGetAllAnnouncementsForUser() throws Exception {
        // Given
        Announcement announcement1 = createAnnouncement("Summary 1", false);
        Announcement announcement2 = createAnnouncement("Summary 2", true);
        List<Announcement> announcements = Arrays.asList(announcement1, announcement2);
        
        when(announcementService.getAnnouncementsByUserId(eq(userId), any()))
            .thenReturn(new SliceImpl<>(announcements, PageRequest.of(1, 5), false));
        when(announcementMapper.toDto(announcement1)).thenReturn(createDto(announcement1));
        when(announcementMapper.toDto(announcement2)).thenReturn(createDto(announcement2));

        // When & Then
        mockMvc.perform(get("/api/v1/announcements/user/{userId}", userId)
                .param("page", "1")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.announcements.length()").value(2))
                .andExpect(jsonPath("$.announcements[0].summary").value("Summary 1"))
                .andExpect(jsonPath("$.announcements[1].summary").value("Summary 2"))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(announcementService).getAnnouncementsByUserId(userId, PageRequest.of(1, 5));
    }

    @Test
    @DisplayName("Should get unread announcements for user")
    void shouldGetUnreadAnnouncementsForUser() throws Exception {
        // Given
        Announcement unreadAnnouncement = createAnnouncement("Unread", false);
        List<Announcement> announcements = Arrays.asList(unreadAnnouncement);
        
        when(announcementService.getUnreadAnnouncementsByUserId(eq(userId), any()))
            .thenReturn(new SliceImpl<>(announcements, PageRequest.of(0, 20), false));
        when(announcementMapper.toDto(unreadAnnouncement)).thenReturn(createDto(unreadAnnouncement));

        // When & Then
        mockMvc.perform(get("/api/v1/announcements/user/{userId}", userId)
                .param("unread", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.announcements.length()").value(1))
                .andExpect(jsonPath("$.announcements[0].summary").value("Unread"))
                .andExpect(jsonPath("$.announcements[0].read").value(false))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(announcementService).getUnreadAnnouncementsByUserId(eq(userId), any());
    }

    @Test
    @DisplayName("Should get read announcements for user")
    void shouldGetReadAnnouncementsForUser() throws Exception {
        // Given
        Announcement readAnnouncement = createAnnouncement("Read", true);
        List<Announcement> announcements = Arrays.asList(readAnnouncement);
        
        when(announcementService.getReadAnnouncementsByUserId(eq(userId), any()))
            .thenReturn(new SliceImpl<>(announcements, PageRequest.of(0, 20), false));
        when(announcementMapper.toDto(readAnnouncement)).thenReturn(createDto(readAnnouncement));

        // When & Then
        mockMvc.perform(get("/api/v1/announcements/user/{userId}", userId)
                .param("unread", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.announcements.length()").value(1))
                .andExpect(jsonPath("$.announcements[0].summary").value("Read"))
                .andExpect(jsonPath("$.announcements[0].read").value(true))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(announcementService).getReadAnnouncementsByUserId(eq(userId), any());
    }

    @Test
    @DisplayName("Should get announcement by ID")
    void shouldGetAnnouncementById() throws Exception {
        // Given
        when(announcementService.getAnnouncementById(announcementId)).thenReturn(testAnnouncement);
        when(announcementMapper.toDto(testAnnouncement)).thenReturn(createDto(testAnnouncement));

        // When & Then
        mockMvc.perform(get("/api/v1/announcements/{id}", announcementId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(announcementId))
                .andExpect(jsonPath("$.summary").value("Test Summary"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(announcementService).getAnnouncementById(announcementId);
    }

    @Test
    @DisplayName("Should mark announcement as read")
    void shouldMarkAnnouncementAsRead() throws Exception {
        // Given
        testAnnouncement.setRead(true);
        when(announcementService.markAsRead(announcementId)).thenReturn(testAnnouncement);
        when(announcementMapper.toDto(testAnnouncement)).thenReturn(createDto(testAnnouncement));

        // When & Then
        mockMvc.perform(patch("/api/v1/announcements/{id}/read", announcementId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(announcementId))
                .andExpect(jsonPath("$.read").value(true));

        verify(announcementService).markAsRead(announcementId);
    }

    @Test
    @DisplayName("Should return empty list when user has no announcements")
    void shouldReturnEmptyListWhenUserHasNoAnnouncements() throws Exception {
        // Given
        when(announcementService.getAnnouncementsByUserId(eq(userId), any()))
            .thenReturn(new SliceImpl<>(Arrays.asList(), PageRequest.of(0, 20), false));

        // When & Then
        mockMvc.perform(get("/api/v1/announcements/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.announcements.length()").value(0))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(announcementService).getAnnouncementsByUserId(eq(userId), any());
    }

    private Announcement createAnnouncement(String summary, boolean read) {
        Announcement announcement = new Announcement();
        announcement.setId(3L);
        announcement.setUser(testUser);
        announcement.setSummary(summary);
        announcement.setDescription("Description for " + summary);
        announcement.setRead(read);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setPublishedAt(LocalDateTime.now());
        return announcement;
    }

    private AnnouncementDto createDto(Announcement announcement) {
        return AnnouncementDto.builder()
            .id(announcement.getId())
            .userId(announcement.getUser().getId())
            .userName(announcement.getUser().getName())
            .read(announcement.isRead())
            .createdAt(announcement.getCreatedAt())
            .description(announcement.getDescription())
            .summary(announcement.getSummary())
            .publishedAt(announcement.getPublishedAt())
            .build();
    }
}
