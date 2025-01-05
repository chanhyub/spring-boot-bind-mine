package com.example.springbootbindmine.modules.record.entity;

import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;
import com.example.springbootbindmine.modules.record.entity.enums.RecordType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Entity
@Table(name = "RECORD")
public class RecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "calendar_idx", referencedColumnName = "idx", updatable = false, nullable = false)
    private CalendarEntity calendar;

    @Column(name = "record_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "create_date", updatable = false, nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;
}
