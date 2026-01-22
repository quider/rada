package pl.factorymethod.rada.announcements.web;

import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.announcements.dto.AnnouncementDto;
import pl.factorymethod.rada.announcements.dto.AnnouncementsSliceDto;
import pl.factorymethod.rada.announcements.mapper.AnnouncementMapper;
import pl.factorymethod.rada.announcements.service.AnnouncementService;
import pl.factorymethod.rada.model.Announcement;

@Slf4j
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementsController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final AnnouncementService announcementService;
    private final AnnouncementMapper announcementMapper;

    /**
     * Get all announcements for a specific user
     * GET /api/v1/announcements/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<AnnouncementsSliceDto> getAnnouncementsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) Boolean unread,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(resolvedPage, resolvedSize);

        log.info("Fetching announcements for user: {}, unread filter: {}, page: {}, size: {}",
            userId, unread, resolvedPage, resolvedSize);
        
        AnnouncementsSliceDto response;

        if (unread != null && unread) {
            Slice<Announcement> announcements = announcementService.getUnreadAnnouncementsByUserId(userId, pageable);
            response = AnnouncementsSliceDto.builder()
                .announcements(announcements.getContent().stream()
                    .map(announcementMapper::toDto)
                    .collect(Collectors.toList()))
                .hasNext(announcements.hasNext())
                .build();
        } else if (unread != null && !unread) {
            Slice<Announcement> announcements = announcementService.getReadAnnouncementsByUserId(userId, pageable);
            response = AnnouncementsSliceDto.builder()
                .announcements(announcements.getContent().stream()
                    .map(announcementMapper::toDto)
                    .collect(Collectors.toList()))
                .hasNext(announcements.hasNext())
                .build();
        } else {
            Slice<Announcement> announcements = announcementService.getAnnouncementsByUserId(userId, pageable);
            response = AnnouncementsSliceDto.builder()
                .announcements(announcements.getContent().stream()
                    .map(announcementMapper::toDto)
                    .collect(Collectors.toList()))
                .hasNext(announcements.hasNext())
                .build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get a single announcement by ID
     * GET /api/v1/announcements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncementById(@PathVariable Long id) {
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
    public ResponseEntity<AnnouncementDto> markAnnouncementAsRead(@PathVariable Long id) {
        log.info("Marking announcement {} as read", id);
        AnnouncementDto announcement = announcementMapper.toDto(
            announcementService.markAsRead(id)
        );
        return ResponseEntity.ok(announcement);
    }
}
