package com.permitseoul.permitserver.user.repository;

import com.permitseoul.permitserver.user.domain.SocialType;
import com.permitseoul.permitserver.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.userId FROM User u WHERE u.socialType = :socialType AND u.socialId = :socialId")
    Optional<Long> findUserIdBySocialTypeAndSocialId(@Param("socialType")final SocialType socialType, @Param("socialId")final String socialId);

    boolean existsBySocialTypeAndSocialId(final SocialType socialType, final String socialId);
}
