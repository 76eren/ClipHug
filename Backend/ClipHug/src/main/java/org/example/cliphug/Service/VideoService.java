package org.example.cliphug.Service;


import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final String VIDEO_DIRECTORY = "videos";
    private final VideoDao videoDao;
    private final UserDao userDao;
    private final ThumbnailService thumbnailService;

    public void storeVideo(Video video) {
        // TODO: This code can go somewhere else they're leftovers from the previous implementation
        byte[] thumbnail = thumbnailService.generateThumbnail(video);
        this.thumbnailService.storeThumbnail(video, thumbnail);
    }


    public ResponseEntity<StreamingResponseBody> getVideoById(UUID id) {
        try {
            Video video = this.videoDao.getVideoById(id);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Path videoPath = Paths.get("videos/"+video.getId()).resolve(video.getFileName()).normalize();
            if (!Files.exists(videoPath) || !Files.isReadable(videoPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            StreamingResponseBody stream = outputStream -> {
                try {
                    Files.copy(videoPath, outputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoPath.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public Video createVideo(Path video, UUID userId) {
        Optional<User> author = this.userDao.findById(userId);
        if (author.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        String fileName = video.getFileName().toString();
        Date currentDate = new Date();
        UUID videoId = UUID.randomUUID();

        Video vid = Video.builder()
                .id(videoId)
                .fileName(fileName)
                .author(author.get())
                .size(-1) // This will be calculated later as the video is uploaded in chunks
                .visibility(VideoVisibility.PUBLIC) // By default, the videos are public
                .uploadData(currentDate)
                .build();

        // Now we assign the video to the user
        List<Video> userVideos = author.get().getVideos();
        userVideos.add(vid);
        author.get().setVideos(userVideos);

        this.videoDao.save(vid);
        this.userDao.save(author.get());

        return vid;
    }


    public void deleteVideo(Video video) throws IOException {
        video.setVisibility(VideoVisibility.DELETED);
        this.videoDao.save(video);

        // Now we delete the video file as it just takes up space
        FileUtils.deleteDirectory(new File("videos/" + video.getId()));

    }


    public void storeChunk(MultipartFile fileChunk, UUID userId, int chunk, int totalChunks, String fileName) throws IOException {
        // We don't want a different video object every chunk, so we store this video object once and then use it
        // A video object only has a reference to all the metadata therefore we can store it at the first chunk
        Video video;
        if (chunk == 0) {
            video = createVideo(Paths.get(fileName), userId);
        }
        else {
            // Since we don't have the video id we cannot retrieve it for each chunk eitherH
            // in the first chunk however the video did get added to the user so from there we can make a reference to the video
            // However this  implementation is not idea as this would mean a user cannot upload two videos at the same time
            Optional<User> author = this.userDao.findById(userId); // Also querying the user for every chunk is probably not ideal either
            if (author.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }
            video = author.get().getVideos().get(author.get().getVideos().size()-1);
        }

        Path tempDir = Paths.get(VIDEO_DIRECTORY, video.getId().toString());
        Files.createDirectories(tempDir);

        Path chunkFile = tempDir.resolve("chunk_" + chunk + ".part");
        fileChunk.transferTo(chunkFile);

        if (areAllChunksUploaded(tempDir, totalChunks)) {
            combineChunks(tempDir, totalChunks, fileName, video);
        }
    }

    private boolean areAllChunksUploaded(Path directory, int totalChunks) throws IOException {
        long count = Files.list(directory).filter(path -> path.toString().endsWith(".part")).count();
        return count == totalChunks;
    }

    private void combineChunks(Path directory, int totalChunks, String fileName, Video video) throws IOException {
        Path outputFile = directory.resolve(fileName);
        try (OutputStream out = Files.newOutputStream(outputFile)) {
            for (int i = 0; i < totalChunks; i++) {
                Path chunkFile = directory.resolve("chunk_" + i + ".part");
                Files.copy(chunkFile, out);
                Files.delete(chunkFile);
            }

            // After all is done we can store the data in the database
            this.storeVideo(video);

            // Now that all the chunks are combined we can calculate the size of the video and add it to the database
            float sizeKB = (float) Files.size(outputFile) / 1024;
            video.setSize(sizeKB);
            video.setFullyUploaded(true); // We can now mark the video as fully uploaded
            this.videoDao.save(video);

        }

    }

    public void deleteNotFullyUploadedVideo(Video video) throws IOException {
        this.deleteVideo(video);

        // Mark the video as deleted
        video.setVisibility(VideoVisibility.DELETED);
        video.setFullyUploaded(true); // I set this to true so that the video gets marked as deleted video like the others and this method won't be called because of an oversight
        this.videoDao.save(video);
    }


}

