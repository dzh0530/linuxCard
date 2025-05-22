package com.linuxdo.cdkdistribution.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cdk")
public class Cdk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private CdkActivity activity;

    @Column(nullable = false)
    private String code;

    @Column(name = "is_claimed", nullable = false)
    private Boolean isClaimed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by")
    private User claimedBy;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 默认构造函数
    public Cdk() {
    }

    // 全参数构造函数
    public Cdk(Long id, CdkActivity activity, String code, Boolean isClaimed, User claimedBy,
              LocalDateTime claimedAt, LocalDateTime createdAt) {
        this.id = id;
        this.activity = activity;
        this.code = code;
        this.isClaimed = isClaimed;
        this.claimedBy = claimedBy;
        this.claimedAt = claimedAt;
        this.createdAt = createdAt;
    }

    // Builder模式的静态内部类
    public static class CdkBuilder {
        private Long id;
        private CdkActivity activity;
        private String code;
        private Boolean isClaimed;
        private User claimedBy;
        private LocalDateTime claimedAt;
        private LocalDateTime createdAt;

        public CdkBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CdkBuilder activity(CdkActivity activity) {
            this.activity = activity;
            return this;
        }

        public CdkBuilder code(String code) {
            this.code = code;
            return this;
        }

        public CdkBuilder isClaimed(Boolean isClaimed) {
            this.isClaimed = isClaimed;
            return this;
        }

        public CdkBuilder claimedBy(User claimedBy) {
            this.claimedBy = claimedBy;
            return this;
        }

        public CdkBuilder claimedAt(LocalDateTime claimedAt) {
            this.claimedAt = claimedAt;
            return this;
        }

        public CdkBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Cdk build() {
            return new Cdk(id, activity, code, isClaimed, claimedBy, claimedAt, createdAt);
        }
    }

    // 静态builder方法
    public static CdkBuilder builder() {
        return new CdkBuilder();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CdkActivity getActivity() {
        return activity;
    }

    public void setActivity(CdkActivity activity) {
        this.activity = activity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsClaimed() {
        return isClaimed;
    }

    public void setIsClaimed(Boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    public User getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(User claimedBy) {
        this.claimedBy = claimedBy;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cdk cdk = (Cdk) o;
        return Objects.equals(id, cdk.id) &&
               Objects.equals(code, cdk.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    // toString方法
    @Override
    public String toString() {
        return "Cdk{" +
                "id=" + id +
                ", activity=" + (activity != null ? activity.getId() : null) +
                ", code='" + code + '\'' +
                ", isClaimed=" + isClaimed +
                ", claimedBy=" + (claimedBy != null ? claimedBy.getId() : null) +
                ", claimedAt=" + claimedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
