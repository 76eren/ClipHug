package org.example.cliphug.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video")
public class Video {
    @Id
    @Column(nullable = false, unique = true)
    @JsonProperty
    private UUID id; // This will be the name of the directory the video is stored in

    @Column(name = "sizeKb")
    @JsonProperty
    private float size = 0; // The size of the video in KB

    @JsonProperty
    @Column(name = "uploadDate")
    private Date uploadData; // The title will be represented by the date of upload

    @JsonProperty
    @Column(name = "fileName")
    private String fileName;

    @ManyToOne
    private User author;

    @JsonProperty
    @Column(name = "visibility")
    private VideoVisibility visibility;

    // Because the video now gets uploaded in chunks if something goes wrong while uploading or the front end for some reason
    // stops uploading the video, we need to know if the video is fully uploaded or not
    // This way we can delete the video if it's not fully uploaded
    @JsonIgnore
    @Column(name = "isFullyUploaded")
    private boolean isFullyUploaded = false;
}


