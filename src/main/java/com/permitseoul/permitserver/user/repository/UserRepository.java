package com.permitseoul.permitserver.user.repository;

import com.permitseoul.permitserver.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
