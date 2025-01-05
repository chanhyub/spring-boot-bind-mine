package com.example.springbootbindmine.modules.record.entity.video;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Entity
@Table(name = "VIDEO_EPISODE_HISTORY")
public class VideoEpisodeHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "video_record_detail_idx", referencedColumnName = "idx", updatable = false, nullable = false)
    private VideoRecordDetailEntity videoRecordDetail;

    @Column(name = "season_number", nullable = false)
    private Integer seasonNumber;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;
}
