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
public class CdkDto {
    private Long id;
    private Long activityId;
    private String code;
    private Boolean isClaimed;
    private UserDto claimedBy;
    private LocalDateTime claimedAt;
}
