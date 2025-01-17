package com.example.springbootbindmine.modules.user.entity;

import com.example.springbootbindmine.modules.user.dto.UserDTO;
import com.example.springbootbindmine.modules.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idx", callSuper = false)
@Entity
@Table(name = "`USER`")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Long idx;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "image_file_link")
    private String imageFileLink;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "create_date", updatable = false, nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    public UserDTO toDTO() {
        return UserDTO.builder()
                .name(this.name)
                .email(this.email)
                .imageFileLink(this.imageFileLink)
                .userName(this.userName)
                .role(this.role)
                .build();
    }
}
