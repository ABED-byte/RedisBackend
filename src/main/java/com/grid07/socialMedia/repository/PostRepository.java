package com.grid07.socialMedia.repository;

import com.grid07.socialMedia.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
