package org.example.cliphug.Dto.Thumbnail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThumbnailRequestDTO {
    private List<String> videoIds;
}
