package org.example.cliphug.Controller;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FrameGrabber;
import org.example.cliphug.Dao.UserDao;
import org.example.cliphug.Dao.VideoDao;
import org.example.cliphug.Dto.Thumbnail.ThumbnailRequestDTO;
import org.example.cliphug.Dto.Video.VideoResponseDTO;
import org.example.cliphug.Dto.Video.VideoUploadDTO;
import org.example.cliphug.Mapper.VideoMapper;
import org.example.cliphug.Model.ApiResponse;
import org.example.cliphug.Model.User;
import org.example.cliphug.Model.Video;
import org.example.cliphug.Model.VideoVisibility;
import org.example.cliphug.Service.AuthenticationService;
import org.example.cliphug.Service.ThumbnailService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    private final AuthenticationService authenticationService;
    private final ThumbnailService thumbnailService;

    @GetMapping()
    public ApiResponse<List<VideoResponseDTO>> getAllVideosFromSelf() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        Optional<User> user = userDao.findById(userId);

        if (user.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        List<VideoResponseDTO> videosResponseDTO = new ArrayList<>();
        for (Video video : user.get().getVideos()) {
            if (video.getVisibility() == VideoVisibility.DELETED) {
                continue;
            }

            if (!video.isFullyUploaded()) {
                continue;
            }

            videosResponseDTO.add(videoMapper.fromEntity(video));
        }

        // Now we sort the videos by upload date latest to oldest
        videosResponseDTO.sort((o1, o2) -> o2.getUploadData().compareTo(o1.getUploadData()));

        return new ApiResponse<>(videosResponseDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{username}")
    public ApiResponse<List<VideoResponseDTO>> getAllVideosFromUserById(@PathVariable String username) throws IOException {
        Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            return new ApiResponse<>("User not found", HttpStatus.NOT_FOUND);
        }

        List<Video> videos = this.videoDao.getVideosByUserId(user.get());
        List<VideoResponseDTO> videosReturnDto = new ArrayList<>();
        for (Video video : videos) {
            if (video.getVisibility() == VideoVisibility.DELETED || video.getVisibility() == VideoVisibility.PRIVATE) {
                continue;
            }

            if (!video.isFullyUploaded()) {
                continue;
            }

            videosReturnDto.add(videoMapper.fromEntity(video));
        }

        return new ApiResponse<>(videosReturnDto);
    }

    @GetMapping(value = "/frame/{id}")
    public ResponseEntity<byte[]> getFirstFrameOfVideo(@PathVariable UUID id) {
        Video video = this.videoDao.getVideoById(id);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (video.getVisibility() == VideoVisibility.DELETED) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (video.getVisibility() == VideoVisibility.PRIVATE) {
            if (!this.authenticationService.checkIfUserIsRequestingTheirOwnData(video.getAuthor().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        }

        byte[] frame = this.thumbnailService.getFirstFrameOfVideo(video);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(frame);
    }

    // Returns multiple frames of the video
    @PostMapping(value = "/frame")
    public ApiResponse<List<byte[]>> getFirstFrameOfVideo(@RequestBody ThumbnailRequestDTO videoIds) {
        if (videoIds.getVideoIds().isEmpty()) {
            return new ApiResponse<>("No video ids provided", HttpStatus.BAD_REQUEST);
        }

        List<byte[]> images = new ArrayList<>();

        for (String videoId : videoIds.getVideoIds()) {
            if (!videoId.matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$")) {
                return new ApiResponse<>("Invalid video id", HttpStatus.BAD_REQUEST);
            }

            byte[] frame = this.thumbnailService.getFirstFrameOfVideo(this.videoDao.getVideoById(UUID.fromString(videoId)));
            if (frame != null) {
                images.add(frame);
            }
        }

        return new ApiResponse<>(images);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<StreamingResponseBody> getVideoById(@PathVariable UUID id) throws IOException {
        Video video = this.videoDao.getVideoById(id);
        if (video == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (video.getVisibility() == VideoVisibility.PRIVATE && !this.authenticationService.checkIfUserIsRequestingTheirOwnData(video.getAuthor().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (video.getVisibility() == VideoVisibility.DELETED) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (!video.isFullyUploaded()) {
            this.videoService.deleteVideo(video);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return this.videoService.getVideoById(id);
    }

    @GetMapping(value = "/data/{id}")
    public ApiResponse<VideoResponseDTO> getVideoDataById(@PathVariable UUID id) throws IOException {
        Video video = this.videoDao.getVideoById(id);
        if (video == null) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        if (video.getVisibility() == VideoVisibility.PRIVATE && !this.authenticationService.checkIfUserIsRequestingTheirOwnData(video.getAuthor().getId())) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        if (video.getVisibility() == VideoVisibility.DELETED) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        if (!video.isFullyUploaded()) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        return new ApiResponse<>(videoMapper.fromEntity(video), HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/{type}")
    public ApiResponse<VideoResponseDTO> changeVideoVisibility(@PathVariable String id, @PathVariable String type) throws IOException {
        // We first check if our type can be converted to a VideoVisibility
        VideoVisibility visibility = VideoVisibility.valueOf(type.toUpperCase());

        Video video = this.videoDao.getVideoById(UUID.fromString(id));
        if (video == null) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        if (video.getVisibility() == VideoVisibility.DELETED) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        // A user shouldn't be able to edit another user's video
        if (!this.authenticationService.checkIfUserIsRequestingTheirOwnData(video.getAuthor().getId())) {
            return new ApiResponse<>("Video not found", HttpStatus.NOT_FOUND);
        }

        if (visibility == VideoVisibility.DELETED) {
            this.videoService.deleteVideo(video);
        }

        video.setVisibility(visibility);
        return new ApiResponse<>(videoMapper.fromEntity(videoDao.save(video)), HttpStatus.OK);
    }


    @PostMapping(value = "/create")
    public ApiResponse<?> createVideo(@RequestParam("file") MultipartFile file,
                                      @RequestParam("chunk") int chunk,
                                      @RequestParam("chunks") int totalChunks,
                                      @RequestParam ("fileName") String fileName)
            throws IOException {
        if (file.isEmpty()) {
            return new ApiResponse<>("No file part", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());

        this.videoService.storeChunk(file, userId, chunk, totalChunks, fileName);
        return new ApiResponse<>("Chunk received", HttpStatus.OK);
    }


}
