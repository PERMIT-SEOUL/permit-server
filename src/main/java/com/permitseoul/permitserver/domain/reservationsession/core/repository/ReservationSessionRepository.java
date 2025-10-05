package com.permitseoul.permitserver.domain.reservationsession.core.repository;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
    Optional<ReservationSessionEntity> findValidSessionByUserIdAndSessionKeyAndValidTime(
            @Param("userId") final long userId,
            @Param("sessionKey") final String sessionKey,
            @Param("validFrom") final LocalDateTime validFrom
    );

    List<ReservationSessionEntity> findAllBySuccessfulTrue();

    List<ReservationSessionEntity> findAllByCreatedAtBefore(final LocalDateTime time);
}
