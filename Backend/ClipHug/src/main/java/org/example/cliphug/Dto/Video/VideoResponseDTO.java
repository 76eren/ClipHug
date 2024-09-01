package org.example.cliphug.Dto.Video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cliphug.Model.VideoVisibility;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDTO {
    private float size;
    private Date uploadData;
    private String fileName;
    private UUID videoId;
    private VideoVisibility visibility;
}
