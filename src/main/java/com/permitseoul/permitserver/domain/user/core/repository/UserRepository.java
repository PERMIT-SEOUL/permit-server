package com.permitseoul.permitserver.domain.user.core.repository;

import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u.userId FROM UserEntity u WHERE u.socialType = :socialType AND u.socialId = :socialId")
    Optional<Long> findUserIdBySocialTypeAndSocialId(@Param("socialType")final SocialType socialType, @Param("socialId")final String socialId);

    boolean existsBySocialTypeAndSocialId(final SocialType socialType, final String socialId);
}
