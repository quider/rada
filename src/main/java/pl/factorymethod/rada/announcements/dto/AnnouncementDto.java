package pl.factorymethod.rada.announcements.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {
    
    private Long id;
    private Long userId;
    private String userName;
    private boolean read;
    private LocalDateTime createdAt;
    private String description;
    private String summary;
    private LocalDateTime publishedAt;
}
