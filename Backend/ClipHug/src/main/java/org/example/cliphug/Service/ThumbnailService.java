package org.example.cliphug.Service;


import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.cliphug.Model.Video;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class ThumbnailService {
    public static final String THUMBNAIL_NAME = "thumbnail.jpg";

    public byte[] getFirstFrameOfVideo(Video video) {
        // We store the first frame of the video as a jpg file called thumbnail.jpg
        // If this file already exists, we just return it, otherwise we create it

        File thumbnail = new File("videos/" + video.getId() + "/" + THUMBNAIL_NAME);
        if (thumbnail.exists()) {
            try {
                return Files.readAllBytes(thumbnail.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            byte[] image = this.generateThumbnail(video);
            this.storeThumbnail(video, image);
            return image;
        }


        return null;
    }

    public byte[] generateThumbnail(Video video) {
        // This is a heavy operation for the server, therefore I decided to store the thumbnails instead as an image
        Java2DFrameConverter converter = null;
        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber("videos/" + video.getId() + "/" + video.getFileName());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            frameGrabber.start();

            Frame frame = frameGrabber.grabImage();

            if (frame != null) {
                converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);

                ImageIO.write(bufferedImage, "jpg", baos);
                baos.flush();
                return baos.toByteArray();
            }

            frameGrabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (converter != null) {
                converter.close();
            }
        }

        return null;
    }

    public void storeThumbnail(Video video, byte[] thumbnail) {
        try {
            Files.write(new File("videos/" + video.getId() + "/"+THUMBNAIL_NAME).toPath(), thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
