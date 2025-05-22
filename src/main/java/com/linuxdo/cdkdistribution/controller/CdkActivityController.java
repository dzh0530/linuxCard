package com.linuxdo.cdkdistribution.controller;

import com.linuxdo.cdkdistribution.dto.ApiResponse;
import com.linuxdo.cdkdistribution.dto.CdkActivityCreateDto;
import com.linuxdo.cdkdistribution.dto.CdkActivityDto;
import com.linuxdo.cdkdistribution.dto.CdkDto;
import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.service.CdkActivityService;
import com.linuxdo.cdkdistribution.service.CdkService;
import com.linuxdo.cdkdistribution.service.UserService;
import com.linuxdo.cdkdistribution.util.CdkParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
public class CdkActivityController {

    private final CdkActivityService cdkActivityService;
    private final CdkService cdkService;
    private final UserService userService;
    private final CdkParser cdkParser;

    @Autowired
    public CdkActivityController(
            CdkActivityService cdkActivityService,
            CdkService cdkService,
            UserService userService,
            CdkParser cdkParser) {
        this.cdkActivityService = cdkActivityService;
        this.cdkService = cdkService;
        this.userService = userService;
        this.cdkParser = cdkParser;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CdkActivityDto>>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CdkActivity> activities = cdkActivityService.getPublicActiveActivities(pageable);
        Page<CdkActivityDto> activityDtos = activities.map(activity -> 
                cdkActivityService.convertToDto(activity, currentUser));
        
        return ResponseEntity.ok(ApiResponse.success(activityDtos));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<CdkActivityDto>>> getAvailableActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CdkActivity> activities = cdkActivityService.getAvailableActivities(currentUser, pageable);
        Page<CdkActivityDto> activityDtos = activities.map(activity -> 
                cdkActivityService.convertToDto(activity, currentUser));
        
        return ResponseEntity.ok(ApiResponse.success(activityDtos));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<CdkActivityDto>>> getMyActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CdkActivity> activities = cdkActivityService.getActivitiesByCreator(currentUser, pageable);
        Page<CdkActivityDto> activityDtos = activities.map(activity -> 
                cdkActivityService.convertToDto(activity, currentUser));
        
        return ResponseEntity.ok(ApiResponse.success(activityDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CdkActivityDto>> getActivity(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        
        Optional<CdkActivity> activityOpt = cdkActivityService.getActivityById(id);
        if (activityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Activity not found"));
        }
        
        CdkActivity activity = activityOpt.get();
        CdkActivityDto activityDto = cdkActivityService.convertToDto(activity, currentUser);
        
        return ResponseEntity.ok(ApiResponse.success(activityDto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TRUSTED', 'LEADER')")
    public ResponseEntity<ApiResponse<CdkActivityDto>> createActivity(
            @Valid @RequestBody CdkActivityCreateDto createDto) {
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        // Parse CDK list if it's a string
        if (createDto.getCdkList() == null || createDto.getCdkList().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CDK list cannot be empty"));
        }
        
        CdkActivity activity = cdkActivityService.createActivity(createDto, currentUser);
        CdkActivityDto activityDto = cdkActivityService.convertToDto(activity, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Activity created successfully", activityDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRUSTED', 'LEADER')")
    public ResponseEntity<ApiResponse<CdkActivityDto>> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody CdkActivityCreateDto updateDto) {
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        try {
            CdkActivity activity = cdkActivityService.updateActivity(id, updateDto, currentUser);
            CdkActivityDto activityDto = cdkActivityService.convertToDto(activity, currentUser);
            
            return ResponseEntity.ok(ApiResponse.success("Activity updated successfully", activityDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRUSTED', 'LEADER')")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        try {
            boolean deleted = cdkActivityService.deleteActivity(id, currentUser);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Activity deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Failed to delete activity"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponse<CdkDto>> claimCdk(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        
        Optional<CdkActivity> activityOpt = cdkActivityService.getActivityById(id);
        if (activityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Activity not found"));
        }
        
        CdkActivity activity = activityOpt.get();
        
        // Check if user has sufficient trust level
        if (currentUser.getTrustLevel() < activity.getMinTrustLevel()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Insufficient trust level"));
        }
        
        // Check if user has already claimed a CDK
        if (cdkService.hasUserClaimedCdk(activity, currentUser)) {
            Optional<Cdk> claimedCdk = cdkService.getUserClaimedCdk(activity, currentUser);
            if (claimedCdk.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(
                        "You have already claimed a CDK from this activity",
                        cdkService.convertToDto(claimedCdk.get())));
            }
        }
        
        // Get client IP address
        String ipAddress = request.getRemoteAddr();
        
        // Claim a CDK
        Optional<Cdk> cdkOpt = cdkService.claimCdk(activity, currentUser, ipAddress);
        
        if (cdkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to claim CDK"));
        }
        
        Cdk cdk = cdkOpt.get();
        return ResponseEntity.ok(ApiResponse.success(
                "CDK claimed successfully",
                cdkService.convertToDto(cdk)));
    }
}
