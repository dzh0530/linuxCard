package com.linuxdo.cdkdistribution.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "claim_record")
public class ClaimRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private CdkActivity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cdk_id", nullable = false)
    private Cdk cdk;

    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    // 默认构造函数
    public ClaimRecord() {
    }

    // 全参数构造函数
    public ClaimRecord(Long id, User user, CdkActivity activity, Cdk cdk, LocalDateTime claimedAt, String ipAddress) {
        this.id = id;
        this.user = user;
        this.activity = activity;
        this.cdk = cdk;
        this.claimedAt = claimedAt;
        this.ipAddress = ipAddress;
    }

    // Builder模式的静态内部类
    public static class ClaimRecordBuilder {
        private Long id;
        private User user;
        private CdkActivity activity;
        private Cdk cdk;
        private LocalDateTime claimedAt;
        private String ipAddress;

        public ClaimRecordBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ClaimRecordBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ClaimRecordBuilder activity(CdkActivity activity) {
            this.activity = activity;
            return this;
        }

        public ClaimRecordBuilder cdk(Cdk cdk) {
            this.cdk = cdk;
            return this;
        }

        public ClaimRecordBuilder claimedAt(LocalDateTime claimedAt) {
            this.claimedAt = claimedAt;
            return this;
        }

        public ClaimRecordBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public ClaimRecord build() {
            return new ClaimRecord(id, user, activity, cdk, claimedAt, ipAddress);
        }
    }

    // 静态builder方法
    public static ClaimRecordBuilder builder() {
        return new ClaimRecordBuilder();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CdkActivity getActivity() {
        return activity;
    }

    public void setActivity(CdkActivity activity) {
        this.activity = activity;
    }

    public Cdk getCdk() {
        return cdk;
    }

    public void setCdk(Cdk cdk) {
        this.cdk = cdk;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimRecord that = (ClaimRecord) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(user.getId(), that.user.getId()) &&
               Objects.equals(activity.getId(), that.activity.getId()) &&
               Objects.equals(cdk.getId(), that.cdk.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user.getId(), activity.getId(), cdk.getId());
    }

    // toString方法
    @Override
    public String toString() {
        return "ClaimRecord{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", activity=" + (activity != null ? activity.getId() : null) +
                ", cdk=" + (cdk != null ? cdk.getId() : null) +
                ", claimedAt=" + claimedAt +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
