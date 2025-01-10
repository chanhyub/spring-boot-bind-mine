package com.example.springbootbindmine.modules.user.dto;

import com.example.springbootbindmine.modules.user.enums.Role;
import lombok.Builder;

@Builder
public record UserDTO(
        String name,
        String email,
        String imageFileLink,
        String userName,
        Role role
) {
}
