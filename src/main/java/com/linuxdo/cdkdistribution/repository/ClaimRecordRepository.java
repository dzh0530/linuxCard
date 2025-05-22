package com.linuxdo.cdkdistribution.repository;

import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.ClaimRecord;
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
public interface ClaimRecordRepository extends JpaRepository<ClaimRecord, Long> {
    
    Page<ClaimRecord> findByUser(User user, Pageable pageable);
    
    Page<ClaimRecord> findByActivity(CdkActivity activity, Pageable pageable);
    
    boolean existsByUserAndActivity(User user, CdkActivity activity);
    
    @Query("SELECT COUNT(cr) FROM ClaimRecord cr WHERE cr.user.id = :userId AND cr.claimedAt >= :since")
    long countRecentClaimsByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(cr) FROM ClaimRecord cr WHERE cr.ipAddress = :ipAddress AND cr.claimedAt >= :since")
    long countRecentClaimsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}
