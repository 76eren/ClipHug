package org.example.cliphug.Controller;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FrameGrabber;
import org.example.cliphug.Dao.UserDao;
import org.example.cliphug.Dao.VideoDao;
import org.example.cliphug.Dto.Video.VideoResponseDTO;
import org.example.cliphug.Dto.Video.VideoUploadDTO;
import org.example.cliphug.Mapper.VideoMapper;
import org.example.cliphug.Model.ApiResponse;
import org.example.cliphug.Model.User;
import org.example.cliphug.Model.Video;
import org.example.cliphug.Service.VideoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoDao videoDao;
    private final VideoService videoService;
    private final UserDao userDao;
    private final VideoMapper videoMapper;

    @PostMapping(value = "/create")
    public ApiResponse<?> createVideo(@ModelAttribute VideoUploadDTO videoUploadDTO) throws IOException {
        if (videoUploadDTO.getVideo() == null) {
            return new ApiResponse<>("No video uploaded", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());

        this.videoService.storeVideo(videoUploadDTO.getVideo(), userId);

        return new ApiResponse<>("Video created", HttpStatus.OK);
    }


    // TODO: Add visibility options
    @GetMapping()
    public ApiResponse<List<VideoResponseDTO>> getAllVideosFromSelf() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        Optional<User> user = userDao.findById(userId);

        if (user.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        List<VideoResponseDTO> videosResponseDTO = new ArrayList<>();
        for (Video video : user.get().getVideos()) {
            videosResponseDTO.add(videoMapper.fromEntity(video));
        }

        return new ApiResponse<>(videosResponseDTO, HttpStatus.OK);
    }

    // This endpoint will be used to generate thumbnails for the various videos, this way we won't have to request all videos for a thumbnail via the frontend
    // TODO: Add visibility options
    @GetMapping(value = "/frame/{id}")
    public ResponseEntity<byte[]> getFirstFrameOfVideo(@PathVariable UUID id) {
        byte[] frame = this.videoService.getFirstFrameOfVideo(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(frame);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Resource> getVideoById(@PathVariable UUID id) {
        return this.videoService.getVideoById(id);
    }


}
