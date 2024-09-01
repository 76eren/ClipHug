package org.example.cliphug.Dao;

import lombok.RequiredArgsConstructor;
import org.example.cliphug.Model.Video;
import org.example.cliphug.Repository.VideoRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VideoDao {
    private static final String VIDEO_DIRECTORY = "videos";
    private final VideoRepository videoRepository;

    public Video save(Video newVideo) {
        return videoRepository.save(newVideo);
    }

    public Video getVideoById(UUID id) {
        return videoRepository.findById(id).orElse(null);
    }

}
