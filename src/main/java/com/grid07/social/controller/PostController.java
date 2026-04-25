package com.grid07.social.controller;

import com.grid07.social.dto.CreatePostRequest;
import com.grid07.social.dto.LikePostRequest;
import com.grid07.social.entity.Post;
import com.grid07.social.service.PostService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // This method creates a post.
    @PostMapping
    public Post createPost(@RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    // This method likes a post and updates virality score in Redis.
    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId, @RequestBody LikePostRequest request) {
        return postService.likePost(postId, request);
    }
}
