package com.example.springbootbindmine.modules.record.repository.video;

import com.example.springbootbindmine.modules.record.entity.video.VideoEpisodeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoEpisodeHistoryRepository extends JpaRepository<VideoEpisodeHistoryEntity, Long> {
}
