package com.permitseoul.permitserver.domain.reservationsession.core.repository;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationSessionRepository extends JpaRepository<ReservationSessionEntity, Long> {

    @Query("""
        SELECT rs
        FROM ReservationSessionEntity rs
        WHERE rs.userId = :userId
          AND rs.sessionKey = :sessionKey
          AND rs.successful = false
          AND rs.createdAt >= :validFrom
    """)
    Optional<ReservationSessionEntity> findValidSession(
            @Param("userId") final long userId,
            @Param("sessionKey") final String sessionKey,
            @Param("validFrom") final LocalDateTime validFrom
    );

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM ReservationSessionEntity rs
        WHERE rs.successful = true
           OR rs.createdAt < :expireThreshold
    """)
    int deleteBySuccessfulTrueOrCreatedAtBefore(@Param("expireThreshold") final LocalDateTime expireThreshold);
}
