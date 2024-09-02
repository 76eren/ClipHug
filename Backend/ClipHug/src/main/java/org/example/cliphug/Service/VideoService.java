package org.example.cliphug.Service;


import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dao.UserDao;
import org.example.cliphug.Dao.VideoDao;
import org.example.cliphug.Model.User;
import org.example.cliphug.Model.Video;
import org.example.cliphug.Model.VideoVisibility;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final String VIDEO_DIRECTORY = "videos";
    private final VideoDao videoDao;
    private final UserDao userDao;

    public void storeVideo(MultipartFile videoFile, UUID userId) throws IOException {
        Video newVideo = createVideo(videoFile);

        Optional<User> user = userDao.findById(userId);

        // This should be impossible
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }


        List<Video> userVideos = user.get().getVideos();
        userVideos.add(newVideo);
        user.get().setVideos(userVideos);


        newVideo.setAuthor(user.get());

        videoDao.save(newVideo);
        userDao.save(user.get());

        storeVideoFile(videoFile, newVideo);
    }

    private void storeVideoFile(MultipartFile videoFile, Video newVideo) throws IOException {
        String directoryPath = VIDEO_DIRECTORY + "/" + newVideo.getId();
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(newVideo.getFileName());
        Files.copy(videoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private Video createVideo(MultipartFile video) {
        String fileName = video.getOriginalFilename();
        Date currentDate = new Date();
        float size = (float) video.getSize() / 1024; // Size in KB

        return Video.builder()
                .fileName(fileName)
                .size(size)
                .visibility(VideoVisibility.PUBLIC) // By default, the videos are public
                .uploadData(currentDate)
                .build();
    }

    public ResponseEntity<Resource> getVideoById(UUID id) {
        try {
            Video video = this.videoDao.getVideoById(id);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Path videoPath = Paths.get("videos/"+video.getId()).resolve(video.getFileName()).normalize();
            Resource resource = new UrlResource(videoPath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public byte[] getFirstFrameOfVideo(Video video) {

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber("videos/" + video.getId() + "/" + video.getFileName())) {
            frameGrabber.start();

            // Grab the first frame
            Frame frame = frameGrabber.grabImage();

            if (frame != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();

                return imageInByte;
            }
            frameGrabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

