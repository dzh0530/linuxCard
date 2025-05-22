package com.linuxdo.cdkdistribution.service.impl;

import com.linuxdo.cdkdistribution.dto.CdkDto;
import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.ClaimRecord;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.repository.CdkActivityRepository;
import com.linuxdo.cdkdistribution.repository.CdkRepository;
import com.linuxdo.cdkdistribution.repository.ClaimRecordRepository;
import com.linuxdo.cdkdistribution.service.CdkService;
import com.linuxdo.cdkdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CdkServiceImpl implements CdkService {

    private final CdkRepository cdkRepository;
    private final CdkActivityRepository cdkActivityRepository;
    private final ClaimRecordRepository claimRecordRepository;
    private final UserService userService;

    @Autowired
    public CdkServiceImpl(
            CdkRepository cdkRepository,
            CdkActivityRepository cdkActivityRepository,
            ClaimRecordRepository claimRecordRepository,
            UserService userService) {
        this.cdkRepository = cdkRepository;
        this.cdkActivityRepository = cdkActivityRepository;
        this.claimRecordRepository = claimRecordRepository;
        this.userService = userService;
    }

    @Override
    public List<Cdk> getCdksByActivity(CdkActivity activity) {
        return cdkRepository.findByActivity(activity);
    }

    @Override
    public List<Cdk> getUnclaimedCdksByActivity(CdkActivity activity) {
        return cdkRepository.findByActivityAndIsClaimed(activity, false);
    }

    @Override
    public List<Cdk> getCdksClaimedByUser(User user) {
        return cdkRepository.findByClaimedBy(user);
    }

    @Override
    @Transactional
    public Optional<Cdk> claimCdk(CdkActivity activity, User user, String ipAddress) {
        // Check if activity is available
        if (!activity.isAvailable()) {
            return Optional.empty();
        }
        
        // Check if user has sufficient trust level
        if (user.getTrustLevel() < activity.getMinTrustLevel()) {
            return Optional.empty();
        }
        
        // Check if user has already claimed a CDK from this activity
        if (hasUserClaimedCdk(activity, user)) {
            return getUserClaimedCdk(activity, user);
        }
        
        // Get the first unclaimed CDK
        Optional<Cdk> cdkOpt = cdkRepository.findFirstUnclaimedByActivityId(activity.getId());
        
        if (cdkOpt.isPresent()) {
            Cdk cdk = cdkOpt.get();
            
            // Mark CDK as claimed
            cdk.setIsClaimed(true);
            cdk.setClaimedBy(user);
            cdk.setClaimedAt(LocalDateTime.now());
            cdkRepository.save(cdk);
            
            // Update activity remaining count
            activity.setRemainingCount(activity.getRemainingCount() - 1);
            cdkActivityRepository.save(activity);
            
            // Create claim record
            ClaimRecord claimRecord = ClaimRecord.builder()
                    .user(user)
                    .activity(activity)
                    .cdk(cdk)
                    .claimedAt(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .build();
            claimRecordRepository.save(claimRecord);
            
            return Optional.of(cdk);
        }
        
        return Optional.empty();
    }

    @Override
    public boolean hasUserClaimedCdk(CdkActivity activity, User user) {
        return claimRecordRepository.existsByUserAndActivity(user, activity);
    }

    @Override
    public Optional<Cdk> getUserClaimedCdk(CdkActivity activity, User user) {
        List<Cdk> cdks = cdkRepository.findByActivityAndIsClaimed(activity, true)
                .stream()
                .filter(cdk -> cdk.getClaimedBy() != null && cdk.getClaimedBy().getId().equals(user.getId()))
                .collect(Collectors.toList());
        
        return cdks.isEmpty() ? Optional.empty() : Optional.of(cdks.get(0));
    }

    @Override
    public CdkDto convertToDto(Cdk cdk) {
        if (cdk == null) {
            return null;
        }
        
        return CdkDto.builder()
                .id(cdk.getId())
                .activityId(cdk.getActivity().getId())
                .code(cdk.getCode())
                .isClaimed(cdk.getIsClaimed())
                .claimedBy(cdk.getClaimedBy() != null ? userService.convertToDto(cdk.getClaimedBy()) : null)
                .claimedAt(cdk.getClaimedAt())
                .build();
    }
}
