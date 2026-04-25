package com.grid07.socialMedia.repository;

import com.grid07.socialMedia.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
