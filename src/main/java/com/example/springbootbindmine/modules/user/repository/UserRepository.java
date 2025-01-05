package com.example.springbootbindmine.modules.user.repository;

import com.example.springbootbindmine.modules.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
