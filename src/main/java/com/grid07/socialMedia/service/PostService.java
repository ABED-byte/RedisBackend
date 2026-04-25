package com.grid07.socialMedia.service;

import com.grid07.socialMedia.dto.CreatePostRequest;
import com.grid07.socialMedia.dto.LikePostRequest;
import com.grid07.socialMedia.entity.Post;
import com.grid07.socialMedia.repository.PostRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final RedisGuardrailService redisGuardrailService;

    public PostService(PostRepository postRepository, RedisGuardrailService redisGuardrailService) {
        this.postRepository = postRepository;
        this.redisGuardrailService = redisGuardrailService;
    }

    // This method creates a new post and saves it to PostgreSQL.
    public Post createPost(CreatePostRequest request) {
        validateAuthorType(request.getAuthorType());

        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setAuthorType(request.getAuthorType());
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // This method records a like as virality points in Redis.
    public String likePost(Long postId, LikePostRequest request) {
        validateAuthorType(request.getAuthorType());
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        redisGuardrailService.incrementViralityScore(postId, request.getAuthorType(), "HUMAN_LIKE");
        return "Post liked successfully";
    }

    // This method validates that author type is only USER or BOT.
    private void validateAuthorType(String authorType) {
        if (!"USER".equals(authorType) && !"BOT".equals(authorType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorType must be USER or BOT");
        }
    }
}
