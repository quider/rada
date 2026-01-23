package pl.factorymethod.rada.announcements.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.announcements.repository.AnnouncementRepository;
import pl.factorymethod.rada.model.Announcement;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    /**
     * Get all announcements for a specific user
     */
    @Transactional(readOnly = true)
    public Slice<Announcement> getAnnouncementsByUserId(String userId, Pageable pageable) {
        log.debug("Fetching all announcements for user: {}", userId);
        return announcementRepository.findByUserId(UUID.fromString(userId)  , pageable);
    }

    /**
     * Get unread announcements for a specific user
     */
    @Transactional(readOnly = true)
    public Slice<Announcement> getUnreadAnnouncementsByUserId(String userId, Pageable pageable) {
        log.debug("Fetching unread announcements for user: {}", userId);
        return announcementRepository.findUnreadByUserId(UUID.fromString(userId), pageable);
    }

    /**
     * Get read announcements for a specific user
     */
    @Transactional(readOnly = true)
    public Slice<Announcement> getReadAnnouncementsByUserId(String userId, Pageable pageable) {
        log.debug("Fetching read announcements for user: {}", userId);
        return announcementRepository.findReadByUserId(UUID.fromString(userId), pageable);
    }

    /**
     * Mark announcement as read
     */
    @Transactional
    public Announcement markAsRead(String announcementId) {
        log.debug("Marking announcement {} as read", announcementId);
        Announcement announcement = announcementRepository.findByPublicId(UUID.fromString(announcementId))
            .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + announcementId));
        announcement.setRead(true);
        return announcementRepository.save(announcement);
    }

    /**
     * Get a single announcement by ID
     */
    @Transactional(readOnly = true)
    public Announcement getAnnouncementById(String id) {
        log.debug("Fetching announcement by id: {}", id);
        return announcementRepository.findByPublicId(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));
    }
}
