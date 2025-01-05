package com.example.springbootbindmine.modules.user.entity;

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

    @Column(name = "oauth_kakao_id")
    private String oauthKakaoId;

    @Column(name = "oauth_apple_id")
    private String oauthAppleId;

    @Column(name = "oauth_google_id")
    private String oauthGoogleId;

    @Column(name = "image_file_link")
    private String imageFileLink;

    @Column(name = "create_date", updatable = false, nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;
}
