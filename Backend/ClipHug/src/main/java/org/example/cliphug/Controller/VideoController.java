package org.example.cliphug.Controller;

import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dao.VideoDao;
import org.example.cliphug.Dto.Video.VideoUploadDTO;
import org.example.cliphug.Model.ApiResponse;
import org.example.cliphug.Service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoDao videoDao;
    private final VideoService videoService;

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


}
