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
public class ClaimRecordDto {
    private Long id;
    private UserDto user;
    private CdkActivityDto activity;
    private CdkDto cdk;
    private LocalDateTime claimedAt;
}
