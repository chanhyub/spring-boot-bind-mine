package com.example.springbootbindmine.modules.record.entity.memo;

import com.example.springbootbindmine.modules.record.entity.RecordEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Entity
@Table(name = "MEMO_RECORD_DETAIL")
public class MemoRecordDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Long idx;

    @OneToOne
    @JoinColumn(name = "record_idx", referencedColumnName = "idx", updatable = false, nullable = false)
    private RecordEntity record;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "quote")
    private String quote;

    @Column(name = "image_file_link")
    private String imageFileLink;

    @Column(name = "create_date", updatable = false, nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;
}
