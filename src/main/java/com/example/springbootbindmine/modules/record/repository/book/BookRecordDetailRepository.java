package com.example.springbootbindmine.modules.record.repository.book;

import com.example.springbootbindmine.modules.record.entity.book.BookRecordDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRecordDetailRepository extends JpaRepository<BookRecordDetailEntity, Long> {
}
