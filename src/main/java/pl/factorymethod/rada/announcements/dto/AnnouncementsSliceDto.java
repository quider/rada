package pl.factorymethod.rada.announcements.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementsSliceDto {

    private List<AnnouncementDto> announcements;
    private boolean hasNext;
}
