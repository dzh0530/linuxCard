package com.linuxdo.cdkdistribution.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cdk_activity")
public class CdkActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "usage_guide", columnDefinition = "TEXT")
    private String usageGuide;

    @Column(name = "min_trust_level", nullable = false)
    private Integer minTrustLevel;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "remaining_count", nullable = false)
    private Integer remainingCount;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 默认构造函数
    public CdkActivity() {
    }

    // 全参数构造函数
    public CdkActivity(Long id, String title, String description, String usageGuide, Integer minTrustLevel,
                      Integer totalCount, Integer remainingCount, LocalDateTime startTime, LocalDateTime endTime,
                      Boolean isActive, Boolean isPublic, User creator, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.usageGuide = usageGuide;
        this.minTrustLevel = minTrustLevel;
        this.totalCount = totalCount;
        this.remainingCount = remainingCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
        this.isPublic = isPublic;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder模式的静态内部类
    public static class CdkActivityBuilder {
        private Long id;
        private String title;
        private String description;
        private String usageGuide;
        private Integer minTrustLevel;
        private Integer totalCount;
        private Integer remainingCount;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Boolean isActive;
        private Boolean isPublic;
        private User creator;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CdkActivityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CdkActivityBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CdkActivityBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CdkActivityBuilder usageGuide(String usageGuide) {
            this.usageGuide = usageGuide;
            return this;
        }

        public CdkActivityBuilder minTrustLevel(Integer minTrustLevel) {
            this.minTrustLevel = minTrustLevel;
            return this;
        }

        public CdkActivityBuilder totalCount(Integer totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public CdkActivityBuilder remainingCount(Integer remainingCount) {
            this.remainingCount = remainingCount;
            return this;
        }

        public CdkActivityBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public CdkActivityBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public CdkActivityBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CdkActivityBuilder isPublic(Boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public CdkActivityBuilder creator(User creator) {
            this.creator = creator;
            return this;
        }

        public CdkActivityBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CdkActivityBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CdkActivity build() {
            return new CdkActivity(id, title, description, usageGuide, minTrustLevel, totalCount, remainingCount,
                                 startTime, endTime, isActive, isPublic, creator, createdAt, updatedAt);
        }
    }

    // 静态builder方法
    public static CdkActivityBuilder builder() {
        return new CdkActivityBuilder();
    }

    // 判断活动是否可用
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && now.isAfter(startTime) && now.isBefore(endTime) && remainingCount > 0;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsageGuide() {
        return usageGuide;
    }

    public void setUsageGuide(String usageGuide) {
        this.usageGuide = usageGuide;
    }

    public Integer getMinTrustLevel() {
        return minTrustLevel;
    }

    public void setMinTrustLevel(Integer minTrustLevel) {
        this.minTrustLevel = minTrustLevel;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
        CdkActivity that = (CdkActivity) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(minTrustLevel, that.minTrustLevel) &&
               Objects.equals(startTime, that.startTime) &&
               Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, minTrustLevel, startTime, endTime);
    }

    // toString方法
    @Override
    public String toString() {
        return "CdkActivity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", usageGuide='" + usageGuide + '\'' +
                ", minTrustLevel=" + minTrustLevel +
                ", totalCount=" + totalCount +
                ", remainingCount=" + remainingCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isActive=" + isActive +
                ", isPublic=" + isPublic +
                ", creator=" + (creator != null ? creator.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
