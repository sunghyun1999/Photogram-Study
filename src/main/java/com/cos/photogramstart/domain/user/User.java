package com.cos.photogramstart.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

// JPA - Java Persistence API (자바로 데이터를 영구적으로 저장(DB)할 수 있는 API를 제공)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity // DB에 테이블을 생성
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    private String website;
    private String bio;
    @Column(nullable = false)
    private String email;
    private String phone;
    private String gender;

    private String profileImageUrl;
    private String role;

    private LocalDateTime createDate;

    @PrePersist // DB에 INSERT 되기 직전에 실행
    public void createDate() {
        this.createDate = LocalDateTime.now();
    }
}
