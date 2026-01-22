package pl.factorymethod.rada.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.factorymethod.rada.model.Announcement;
import pl.factorymethod.rada.model.School;
import pl.factorymethod.rada.model.SchoolClass;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.User;

@SpringBootTest(classes = {pl.factorymethod.rada.RadaApplication.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=false"
})
@Transactional
@DisplayName("AnnouncementRepository Tests")
class AnnouncementRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AnnouncementRepository announcementRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Create test school
        School school = new School();
        school.setName("Test School " + UUID.randomUUID());
        school.setAddress("Test Address");
        entityManager.persist(school);
        entityManager.flush();

        // Create test class
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName("Class 1A");
        schoolClass.setStartYear(2024);
        schoolClass.setSchool(school);
        entityManager.persist(schoolClass);
        entityManager.flush();

        // Create test students
        Student student1 = new Student();
        student1.setNumber("STU001");
        student1.setSchoolClass(schoolClass);
        entityManager.persist(student1);
        entityManager.flush();

        Student student2 = new Student();
        student2.setNumber("STU002");
        student2.setSchoolClass(schoolClass);
        entityManager.persist(student2);
        entityManager.flush();

        // Create test users
        testUser1 = new User();
        testUser1.setStudent(student1);
        testUser1.setName("Test User 1");
        testUser1.setEmail("user1@test.com");
        testUser1.setPhone("123456789");
        testUser1.setPassword("password");
        entityManager.persist(testUser1);
        entityManager.flush();

        testUser2 = new User();
        testUser2.setStudent(student2);
        testUser2.setName("Test User 2");
        testUser2.setEmail("user2@test.com");
        testUser2.setPhone("987654321");
        testUser2.setPassword("password");
        entityManager.persist(testUser2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all announcements by user ID")
    void shouldFindAllAnnouncementsByUserId() {
        // Given
        createAnnouncement(testUser1, "Summary 1", "Description 1", false);
        createAnnouncement(testUser1, "Summary 2", "Description 2", true);
        createAnnouncement(testUser2, "Summary 3", "Description 3", false);
        entityManager.flush();

        // When
        List<Announcement> announcements = announcementRepository
            .findByUserId(testUser1.getId(), PageRequest.of(0, 20))
            .getContent();

        // Then
        assertThat(announcements).hasSize(2);
        assertThat(announcements)
            .extracting(Announcement::getSummary)
            .containsExactlyInAnyOrder("Summary 2", "Summary 1");
    }

    @Test
    @DisplayName("Should find unread announcements by user ID")
    void shouldFindUnreadAnnouncementsByUserId() {
        // Given
        createAnnouncement(testUser1, "Unread 1", "Description 1", false);
        createAnnouncement(testUser1, "Read 1", "Description 2", true);
        createAnnouncement(testUser1, "Unread 2", "Description 3", false);
        entityManager.flush();

        // When
        List<Announcement> unreadAnnouncements = announcementRepository
            .findUnreadByUserId(testUser1.getId(), PageRequest.of(0, 20))
            .getContent();

        // Then
        assertThat(unreadAnnouncements).hasSize(2);
        assertThat(unreadAnnouncements)
            .extracting(Announcement::getSummary)
            .containsExactlyInAnyOrder("Unread 2", "Unread 1");
        assertThat(unreadAnnouncements).allMatch(a -> !a.isRead());
    }

    @Test
    @DisplayName("Should find read announcements by user ID")
    void shouldFindReadAnnouncementsByUserId() {
        // Given
        createAnnouncement(testUser1, "Unread 1", "Description 1", false);
        createAnnouncement(testUser1, "Read 1", "Description 2", true);
        createAnnouncement(testUser1, "Read 2", "Description 3", true);
        entityManager.flush();

        // When
        List<Announcement> readAnnouncements = announcementRepository
            .findReadByUserId(testUser1.getId(), PageRequest.of(0, 20))
            .getContent();

        // Then
        assertThat(readAnnouncements).hasSize(2);
        assertThat(readAnnouncements)
            .extracting(Announcement::getSummary)
            .containsExactlyInAnyOrder("Read 2", "Read 1");
        assertThat(readAnnouncements).allMatch(Announcement::isRead);
    }

    @Test
    @DisplayName("Should return empty list when user has no announcements")
    void shouldReturnEmptyListWhenUserHasNoAnnouncements() {
        // When
        List<Announcement> announcements = announcementRepository
            .findByUserId(testUser1.getId(), PageRequest.of(0, 20))
            .getContent();

        // Then
        assertThat(announcements).isEmpty();
    }

    @Test
    @DisplayName("Should order announcements by published date descending")
    void shouldOrderAnnouncementsByPublishedDateDescending() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Announcement old = createAnnouncementWithDate(testUser1, "Old", now.minusDays(2));
        Announcement recent = createAnnouncementWithDate(testUser1, "Recent", now.minusHours(1));
        Announcement newest = createAnnouncementWithDate(testUser1, "Newest", now);
        entityManager.flush();

        // When
        List<Announcement> announcements = announcementRepository
            .findByUserId(testUser1.getId(), PageRequest.of(0, 20))
            .getContent();

        // Then
        assertThat(announcements).hasSize(3);
        assertThat(announcements.get(0).getSummary()).isEqualTo("Newest");
        assertThat(announcements.get(1).getSummary()).isEqualTo("Recent");
        assertThat(announcements.get(2).getSummary()).isEqualTo("Old");
    }

    private Announcement createAnnouncement(User user, String summary, String description, boolean read) {
        Announcement announcement = new Announcement();
        announcement.setUser(user);
        announcement.setSummary(summary);
        announcement.setDescription(description);
        announcement.setRead(read);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setPublishedAt(LocalDateTime.now());
        entityManager.persist(announcement);
        entityManager.flush();
        return announcement;
    }

    private Announcement createAnnouncementWithDate(User user, String summary, LocalDateTime publishedAt) {
        Announcement announcement = new Announcement();
        announcement.setUser(user);
        announcement.setSummary(summary);
        announcement.setDescription("Description");
        announcement.setRead(false);
        announcement.setCreatedAt(publishedAt);
        announcement.setPublishedAt(publishedAt);
        entityManager.persist(announcement);
        entityManager.flush();
        return announcement;
    }
}
