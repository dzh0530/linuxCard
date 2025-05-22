package com.linuxdo.cdkdistribution.service.impl;

import com.linuxdo.cdkdistribution.dto.CdkActivityCreateDto;
import com.linuxdo.cdkdistribution.dto.CdkActivityDto;
import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.repository.CdkActivityRepository;
import com.linuxdo.cdkdistribution.repository.CdkRepository;
import com.linuxdo.cdkdistribution.service.CdkActivityService;
import com.linuxdo.cdkdistribution.service.UserService;
import com.linuxdo.cdkdistribution.util.CdkParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CdkActivityServiceImpl implements CdkActivityService {

    private final CdkActivityRepository cdkActivityRepository;
    private final CdkRepository cdkRepository;
    private final UserService userService;
    private final CdkParser cdkParser;

    @Autowired
    public CdkActivityServiceImpl(
            CdkActivityRepository cdkActivityRepository,
            CdkRepository cdkRepository,
            UserService userService,
            CdkParser cdkParser) {
        this.cdkActivityRepository = cdkActivityRepository;
        this.cdkRepository = cdkRepository;
        this.userService = userService;
        this.cdkParser = cdkParser;
    }

    @Override
    @Transactional
    public CdkActivity createActivity(CdkActivityCreateDto createDto, User creator) {
        // Parse CDK list
        List<String> cdkCodes = createDto.getCdkList();
        int totalCount = cdkCodes.size();
        
        // Create activity
        CdkActivity activity = CdkActivity.builder()
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .usageGuide(createDto.getUsageGuide())
                .minTrustLevel(createDto.getMinTrustLevel())
                .totalCount(totalCount)
                .remainingCount(totalCount)
                .startTime(createDto.getStartTime())
                .endTime(createDto.getEndTime())
                .isActive(true)
                .isPublic(createDto.getIsPublic())
                .creator(creator)
                .build();
        
        CdkActivity savedActivity = cdkActivityRepository.save(activity);
        
        // Create CDKs
        List<Cdk> cdkEntities = new ArrayList<>();
        for (String code : cdkCodes) {
            Cdk cdk = Cdk.builder()
                    .activity(savedActivity)
                    .code(code)
                    .isClaimed(false)
                    .build();
            cdkEntities.add(cdk);
        }
        
        cdkRepository.saveAll(cdkEntities);
        
        return savedActivity;
    }

    @Override
    public Optional<CdkActivity> getActivityById(Long id) {
        return cdkActivityRepository.findById(id);
    }

    @Override
    public Page<CdkActivity> getActivitiesByCreator(User creator, Pageable pageable) {
        return cdkActivityRepository.findByCreator(creator, pageable);
    }

    @Override
    public Page<CdkActivity> getPublicActiveActivities(Pageable pageable) {
        return cdkActivityRepository.findByIsActiveAndIsPublic(true, true, pageable);
    }

    @Override
    public Page<CdkActivity> getAvailableActivities(User user, Pageable pageable) {
        if (user == null) {
            return Page.empty(pageable);
        }
        
        return cdkActivityRepository.findAvailableActivities(
                LocalDateTime.now(),
                user.getTrustLevel(),
                pageable
        );
    }

    @Override
    @Transactional
    public CdkActivity updateActivity(Long id, CdkActivityCreateDto updateDto, User currentUser) {
        CdkActivity activity = cdkActivityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + id));
        
        // Check if the current user is the creator
        if (!activity.getCreator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to update this activity");
        }
        
        // Update activity details
        activity.setTitle(updateDto.getTitle());
        activity.setDescription(updateDto.getDescription());
        activity.setUsageGuide(updateDto.getUsageGuide());
        activity.setMinTrustLevel(updateDto.getMinTrustLevel());
        activity.setStartTime(updateDto.getStartTime());
        activity.setEndTime(updateDto.getEndTime());
        activity.setIsPublic(updateDto.getIsPublic());
        
        return cdkActivityRepository.save(activity);
    }

    @Override
    @Transactional
    public boolean deleteActivity(Long id, User currentUser) {
        CdkActivity activity = cdkActivityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + id));
        
        // Check if the current user is the creator
        if (!activity.getCreator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this activity");
        }
        
        cdkActivityRepository.delete(activity);
        return true;
    }

    @Override
    public CdkActivityDto convertToDto(CdkActivity activity, User currentUser) {
        if (activity == null) {
            return null;
        }
        
        boolean canClaim = false;
        boolean hasClaimed = false;
        
        if (currentUser != null) {
            // Check if user can claim
            canClaim = activity.isAvailable() && 
                      currentUser.getTrustLevel() >= activity.getMinTrustLevel() &&
                      !cdkRepository.existsByActivityAndClaimedBy(activity, currentUser);
            
            // Check if user has already claimed
            hasClaimed = cdkRepository.existsByActivityAndClaimedBy(activity, currentUser);
        }
        
        return CdkActivityDto.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .usageGuide(activity.getUsageGuide())
                .minTrustLevel(activity.getMinTrustLevel())
                .totalCount(activity.getTotalCount())
                .remainingCount(activity.getRemainingCount())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .isActive(activity.getIsActive())
                .isPublic(activity.getIsPublic())
                .creator(userService.convertToDto(activity.getCreator()))
                .createdAt(activity.getCreatedAt())
                .canClaim(canClaim)
                .hasClaimed(hasClaimed)
                .build();
    }
}
