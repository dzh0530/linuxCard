package com.linuxdo.cdkdistribution.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "linux_do_id", unique = true, nullable = false)
    private String linuxDoId;

    @Column(nullable = false)
    private String username;

    @Column
    private String avatar;

    @Column(name = "trust_level", nullable = false)
    private Integer trustLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 默认构造函数
    public User() {
    }

    // 全参数构造函数
    public User(Long id, String linuxDoId, String username, String avatar, Integer trustLevel,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.linuxDoId = linuxDoId;
        this.username = username;
        this.avatar = avatar;
        this.trustLevel = trustLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder模式的静态内部类
    public static class UserBuilder {
        private Long id;
        private String linuxDoId;
        private String username;
        private String avatar;
        private Integer trustLevel;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder linuxDoId(String linuxDoId) {
            this.linuxDoId = linuxDoId;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public UserBuilder trustLevel(Integer trustLevel) {
            this.trustLevel = trustLevel;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(id, linuxDoId, username, avatar, trustLevel, createdAt, updatedAt);
        }
    }

    // 静态builder方法
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinuxDoId() {
        return linuxDoId;
    }

    public void setLinuxDoId(String linuxDoId) {
        this.linuxDoId = linuxDoId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(Integer trustLevel) {
        this.trustLevel = trustLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(linuxDoId, user.linuxDoId) &&
               Objects.equals(username, user.username) &&
               Objects.equals(avatar, user.avatar) &&
               Objects.equals(trustLevel, user.trustLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, linuxDoId, username, avatar, trustLevel);
    }

    // toString方法
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", linuxDoId='" + linuxDoId + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", trustLevel=" + trustLevel +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
