package com.example.springbootbindmine.modules.record.repository;

import com.example.springbootbindmine.modules.record.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
}
