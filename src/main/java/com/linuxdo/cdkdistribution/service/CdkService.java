package com.linuxdo.cdkdistribution.service;

import com.linuxdo.cdkdistribution.dto.CdkDto;
import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;

import java.util.List;
import java.util.Optional;

public interface CdkService {
    
    /**
     * Get all CDKs for an activity
     *
     * @param activity The activity
     * @return List of CDKs
     */
    List<Cdk> getCdksByActivity(CdkActivity activity);
    
    /**
     * Get all unclaimed CDKs for an activity
     *
     * @param activity The activity
     * @return List of unclaimed CDKs
     */
    List<Cdk> getUnclaimedCdksByActivity(CdkActivity activity);
    
    /**
     * Get all CDKs claimed by a user
     *
     * @param user The user
     * @return List of claimed CDKs
     */
    List<Cdk> getCdksClaimedByUser(User user);
    
    /**
     * Claim a CDK from an activity for a user
     *
     * @param activity The activity
     * @param user The user
     * @param ipAddress The IP address of the user
     * @return The claimed CDK
     */
    Optional<Cdk> claimCdk(CdkActivity activity, User user, String ipAddress);
    
    /**
     * Check if a user has already claimed a CDK from an activity
     *
     * @param activity The activity
     * @param user The user
     * @return True if the user has already claimed a CDK
     */
    boolean hasUserClaimedCdk(CdkActivity activity, User user);
    
    /**
     * Get a CDK claimed by a user for an activity
     *
     * @param activity The activity
     * @param user The user
     * @return Optional containing the CDK if found
     */
    Optional<Cdk> getUserClaimedCdk(CdkActivity activity, User user);
    
    /**
     * Convert a Cdk entity to DTO
     *
     * @param cdk The CDK entity
     * @return The CDK DTO
     */
    CdkDto convertToDto(Cdk cdk);
}
