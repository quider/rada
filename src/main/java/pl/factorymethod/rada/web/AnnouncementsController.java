package pl.factorymethod.rada.web;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.dto.AnnouncementDto;
import pl.factorymethod.rada.mapper.AnnouncementMapper;
import pl.factorymethod.rada.service.AnnouncementService;

@Slf4j
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementsController {

    private final AnnouncementService announcementService;
    private final AnnouncementMapper announcementMapper;

    /**
     * Get all announcements for a specific user
     * GET /api/v1/announcements/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncementsByUserId(
            @PathVariable UUID userId,
            @RequestParam(required = false) Boolean unread) {
        
        log.info("Fetching announcements for user: {}, unread filter: {}", userId, unread);
        
        List<AnnouncementDto> announcements;
        
        if (unread != null && unread) {
            announcements = announcementService.getUnreadAnnouncementsByUserId(userId)
                .stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
        } else if (unread != null && !unread) {
            announcements = announcementService.getReadAnnouncementsByUserId(userId)
                .stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
        } else {
            announcements = announcementService.getAnnouncementsByUserId(userId)
                .stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(announcements);
    }

    /**
     * Get a single announcement by ID
     * GET /api/v1/announcements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncementById(@PathVariable UUID id) {
        log.info("Fetching announcement by id: {}", id);
        AnnouncementDto announcement = announcementMapper.toDto(
            announcementService.getAnnouncementById(id)
        );
        return ResponseEntity.ok(announcement);
    }

    /**
     * Mark announcement as read
     * PATCH /api/v1/announcements/{id}/read
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<AnnouncementDto> markAnnouncementAsRead(@PathVariable UUID id) {
        log.info("Marking announcement {} as read", id);
        AnnouncementDto announcement = announcementMapper.toDto(
            announcementService.markAsRead(id)
        );
        return ResponseEntity.ok(announcement);
    }
}
