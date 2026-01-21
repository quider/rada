package pl.factorymethod.rada.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {
    
    private UUID id;
    private UUID userId;
    private String userName;
    private boolean read;
    private LocalDateTime createdAt;
    private String description;
    private String summary;
    private LocalDateTime publishedAt;
}
