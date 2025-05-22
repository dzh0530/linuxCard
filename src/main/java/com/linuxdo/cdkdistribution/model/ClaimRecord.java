package com.linuxdo.cdkdistribution.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
