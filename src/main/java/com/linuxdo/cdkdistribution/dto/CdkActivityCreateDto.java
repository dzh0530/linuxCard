package com.linuxdo.cdkdistribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkActivityCreateDto {
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Usage guide is required")
    private String usageGuide;
    
    @NotNull(message = "Minimum trust level is required")
    @Min(value = 1, message = "Minimum trust level must be at least 1")
    @Max(value = 3, message = "Minimum trust level must be at most 3")
    private Integer minTrustLevel;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    @NotNull(message = "Is public flag is required")
    private Boolean isPublic;
    
    @NotEmpty(message = "CDK list cannot be empty")
    private List<String> cdkList;
}
