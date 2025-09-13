package com.permitseoul.permitserver.domain.auth.core.repository;

import com.permitseoul.permitserver.domain.auth.core.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
