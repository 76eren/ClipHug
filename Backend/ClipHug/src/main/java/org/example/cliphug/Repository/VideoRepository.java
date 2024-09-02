package org.example.cliphug.Repository;

import org.example.cliphug.Model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    List<Video> findByAuthor_Id(UUID userId);
}
