package org.example.cliphug.Mapper;


import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dto.Video.VideoResponseDTO;
import org.example.cliphug.Model.Video;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoMapper {

    public VideoResponseDTO fromEntity(Video video) {
        return VideoResponseDTO
                .builder()
                .fileName(video.getFileName())
                .size(video.getSize())
                .uploadData(video.getUploadData())
                .videoId(video.getId())
                .build();
    }

}
