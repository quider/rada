package pl.factorymethod.rada.mapper;

import org.springframework.stereotype.Component;

import pl.factorymethod.rada.dto.AnnouncementDto;
import pl.factorymethod.rada.model.Announcement;

@Component
public class AnnouncementMapper {

    public AnnouncementDto toDto(Announcement announcement) {
        if (announcement == null) {
            return null;
        }

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
