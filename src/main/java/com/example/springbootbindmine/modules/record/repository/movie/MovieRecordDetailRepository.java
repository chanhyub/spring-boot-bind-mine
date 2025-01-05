package com.example.springbootbindmine.modules.record.repository.movie;

import com.example.springbootbindmine.modules.record.entity.movie.MovieRecordDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRecordDetailRepository extends JpaRepository<MovieRecordDetailEntity, Long> {
}
