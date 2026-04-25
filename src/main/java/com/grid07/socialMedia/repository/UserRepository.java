package com.grid07.socialMedia.repository;

import com.grid07.socialMedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
