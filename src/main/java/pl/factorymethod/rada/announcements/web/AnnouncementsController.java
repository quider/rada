package pl.factorymethod.rada.announcements.web;

import java.util.List;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.CrossOrigin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.announcements.dto.AnnouncementDto;
import pl.factorymethod.rada.announcements.mapper.AnnouncementMapper;
import pl.factorymethod.rada.announcements.service.AnnouncementService;
import pl.factorymethod.rada.model.Announcement;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Announcement management APIs")
public class AnnouncementsController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final AnnouncementService announcementService;
    private final AnnouncementMapper announcementMapper;

    /**
     * Get all announcements for a specific user
     * GET /api/v1/announcements/user/{userId}
     */
    @Operation(
            summary = "Get user announcements",
            description = "Retrieve paginated announcements for a specific user with optional unread filter"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved announcements",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AnnouncementDto.class)))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncementsByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable String userId,
            @Parameter(description = "Filter for unread/read announcements") @RequestParam(required = false) Boolean unread,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "2") int size) {
        
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(resolvedPage, resolvedSize);

        log.info("Fetching announcements for user: {}, unread filter: {}, page: {}, size: {}",
            userId, unread, resolvedPage, resolvedSize);
        
        List<AnnouncementDto> response;

        if (unread != null && unread) {
            Slice<Announcement> announcements = announcementService.getUnreadAnnouncementsByUserId(userId, pageable);
            response = announcements.getContent().stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
        } else if (unread != null && !unread) {
            Slice<Announcement> announcements = announcementService.getReadAnnouncementsByUserId(userId, pageable);
            response = announcements.getContent().stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
        } else {
            Slice<Announcement> announcements = announcementService.getAnnouncementsByUserId(userId, pageable);
            response = announcements.getContent().stream()
                .map(announcementMapper::toDto)
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get a single announcement by ID
     * GET /api/v1/announcements/{id}
     */
    @Operation(
            summary = "Get announcement by ID",
            description = "Retrieve a specific announcement by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved announcement",
                    content = @Content(schema = @Schema(implementation = AnnouncementDto.class))),
            @ApiResponse(responseCode = "404", description = "Announcement not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncementById(
            @Parameter(description = "Announcement ID", required = true) @PathVariable String id) {
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
    @Operation(
            summary = "Mark announcement as read",
            description = "Update an announcement's status to mark it as read"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully marked as read",
                    content = @Content(schema = @Schema(implementation = AnnouncementDto.class))),
            @ApiResponse(responseCode = "404", description = "Announcement not found",
                    content = @Content)
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<AnnouncementDto> markAnnouncementAsRead(
            @Parameter(description = "Announcement ID", required = true) @PathVariable String id) {
        log.info("Marking announcement {} as read", id);
        AnnouncementDto announcement = announcementMapper.toDto(
            announcementService.markAsRead(id)
        );
        return ResponseEntity.ok(announcement);
    }
}
