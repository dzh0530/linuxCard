package com.linuxdo.cdkdistribution.service;

import com.linuxdo.cdkdistribution.dto.CdkActivityCreateDto;
import com.linuxdo.cdkdistribution.dto.CdkActivityDto;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CdkActivityService {
    
    /**
     * Create a new CDK activity
     *
     * @param createDto The DTO containing activity details
     * @param creator The user creating the activity
     * @return The created activity
     */
    CdkActivity createActivity(CdkActivityCreateDto createDto, User creator);
    
    /**
     * Get an activity by ID
     *
     * @param id The activity ID
     * @return Optional containing the activity if found
     */
    Optional<CdkActivity> getActivityById(Long id);
    
    /**
     * Get all activities created by a user
     *
     * @param creator The creator user
     * @param pageable Pagination information
     * @return Page of activities
     */
    Page<CdkActivity> getActivitiesByCreator(User creator, Pageable pageable);
    
    /**
     * Get all public and active activities
     *
     * @param pageable Pagination information
     * @return Page of activities
     */
    Page<CdkActivity> getPublicActiveActivities(Pageable pageable);
    
    /**
     * Get activities available for a user based on trust level
     *
     * @param user The user
     * @param pageable Pagination information
     * @return Page of available activities
     */
    Page<CdkActivity> getAvailableActivities(User user, Pageable pageable);
    
    /**
     * Update an activity
     *
     * @param id The activity ID
     * @param updateDto The DTO containing updated details
     * @param currentUser The current user
     * @return The updated activity
     */
    CdkActivity updateActivity(Long id, CdkActivityCreateDto updateDto, User currentUser);
    
    /**
     * Delete an activity
     *
     * @param id The activity ID
     * @param currentUser The current user
     * @return True if deleted successfully
     */
    boolean deleteActivity(Long id, User currentUser);
    
    /**
     * Convert a CdkActivity entity to DTO
     *
     * @param activity The activity entity
     * @param currentUser The current user (for checking claim status)
     * @return The activity DTO
     */
    CdkActivityDto convertToDto(CdkActivity activity, User currentUser);
}
