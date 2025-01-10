package com.example.springbootbindmine.common.security.dto;

import com.example.springbootbindmine.modules.user.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public record CustomOAuthUser(
        UserDTO userDTO
) implements OAuth2User {
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> userDTO.role().name());

        return collection;
    }

    @Override
    public String getName() {
        return userDTO.userName();
    }
}
