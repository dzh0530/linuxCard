package com.linuxdo.cdkdistribution.repository;

import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CdkActivityRepository extends JpaRepository<CdkActivity, Long> {
    
    Page<CdkActivity> findByCreator(User creator, Pageable pageable);
    
    Page<CdkActivity> findByIsActiveAndIsPublic(Boolean isActive, Boolean isPublic, Pageable pageable);
    
    @Query("SELECT ca FROM CdkActivity ca WHERE ca.isActive = true AND ca.isPublic = true " +
           "AND ca.startTime <= :now AND ca.endTime >= :now AND ca.remainingCount > 0 " +
           "AND ca.minTrustLevel <= :trustLevel")
    Page<CdkActivity> findAvailableActivities(@Param("now") LocalDateTime now, 
                                             @Param("trustLevel") Integer trustLevel, 
                                             Pageable pageable);
    
    @Query("SELECT ca FROM CdkActivity ca WHERE ca.isActive = true " +
           "AND ca.startTime <= :now AND ca.endTime >= :now " +
           "AND ca.creator.id = :creatorId")
    List<CdkActivity> findActiveActivitiesByCreator(@Param("now") LocalDateTime now, 
                                                  @Param("creatorId") Long creatorId);
}
