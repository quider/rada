package pl.factorymethod.rada.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.model.Announcement;
import pl.factorymethod.rada.repository.AnnouncementRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    /**
     * Get all announcements for a specific user
     */
    @Transactional(readOnly = true)
    public List<Announcement> getAnnouncementsByUserId(UUID userId) {
        log.debug("Fetching all announcements for user: {}", userId);
        return announcementRepository.findByUserId(userId);
    }

    /**
     * Get unread announcements for a specific user
     */
    @Transactional(readOnly = true)
    public List<Announcement> getUnreadAnnouncementsByUserId(UUID userId) {
        log.debug("Fetching unread announcements for user: {}", userId);
        return announcementRepository.findUnreadByUserId(userId);
    }

    /**
     * Get read announcements for a specific user
     */
    @Transactional(readOnly = true)
    public List<Announcement> getReadAnnouncementsByUserId(UUID userId) {
        log.debug("Fetching read announcements for user: {}", userId);
        return announcementRepository.findReadByUserId(userId);
    }

    /**
     * Mark announcement as read
     */
    @Transactional
    public Announcement markAsRead(UUID announcementId) {
        log.debug("Marking announcement {} as read", announcementId);
        Announcement announcement = announcementRepository.findById(announcementId)
            .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + announcementId));
        announcement.setRead(true);
        return announcementRepository.save(announcement);
    }

    /**
     * Get a single announcement by ID
     */
    @Transactional(readOnly = true)
    public Announcement getAnnouncementById(UUID id) {
        log.debug("Fetching announcement by id: {}", id);
        return announcementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));
    }
}
