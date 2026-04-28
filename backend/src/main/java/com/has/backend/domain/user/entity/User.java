package com.has.backend.domain.user.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.has.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    private User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.email = "deleted_" + NanoIdUtils.randomNanoId() + "_" + this.email;
    }
}
