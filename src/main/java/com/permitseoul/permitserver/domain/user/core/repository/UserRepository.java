package com.permitseoul.permitserver.domain.user.core.repository;

import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.socialType = :socialType AND u.socialId = :socialId")
    Optional<UserEntity> findUserBySocialTypeAndSocialId(@Param("socialType") final SocialType socialType,
                                                         @Param("socialId") final String socialId);

    boolean existsBySocialTypeAndSocialId(final SocialType socialType, final String socialId);

    boolean existsByEmail(final String userEmail);
}
