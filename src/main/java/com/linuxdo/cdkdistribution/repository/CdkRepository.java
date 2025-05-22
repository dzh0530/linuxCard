package com.linuxdo.cdkdistribution.repository;

import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CdkRepository extends JpaRepository<Cdk, Long> {
    
    List<Cdk> findByActivity(CdkActivity activity);
    
    List<Cdk> findByActivityAndIsClaimed(CdkActivity activity, Boolean isClaimed);
    
    List<Cdk> findByClaimedBy(User user);
    
    @Query("SELECT c FROM Cdk c WHERE c.activity.id = :activityId AND c.isClaimed = false LIMIT 1")
    Optional<Cdk> findFirstUnclaimedByActivityId(@Param("activityId") Long activityId);
    
    long countByActivityAndIsClaimed(CdkActivity activity, Boolean isClaimed);
    
    boolean existsByActivityAndClaimedBy(CdkActivity activity, User user);
}
