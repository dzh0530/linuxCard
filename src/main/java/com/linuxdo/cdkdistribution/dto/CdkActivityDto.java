package com.linuxdo.cdkdistribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkActivityDto {
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
    private UserDto creator;
    private LocalDateTime createdAt;
    private Boolean canClaim;
    private Boolean hasClaimed;
}
