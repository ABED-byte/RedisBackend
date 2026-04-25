package com.grid07.social.controller;

import com.grid07.social.dto.CreateCommentRequest;
import com.grid07.social.entity.Comment;
import com.grid07.social.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // This method creates a comment and handles bot guardrail failures with HTTP 429.
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CreateCommentRequest request) {
        if ("BOT".equals(request.getAuthorType())) {
            try {
                Comment comment = commentService.createComment(postId, request);
                return ResponseEntity.ok(comment);
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Bot limit reached or cooldown active");
            }
        }

        Comment comment = commentService.createComment(postId, request);
        return ResponseEntity.ok(comment);
    }
}
