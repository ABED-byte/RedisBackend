package com.grid07.social.service;

import com.grid07.social.dto.CreateCommentRequest;
import com.grid07.social.entity.Comment;
import com.grid07.social.entity.Post;
import com.grid07.social.repository.BotRepository;
import com.grid07.social.repository.CommentRepository;
import com.grid07.social.repository.PostRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BotRepository botRepository;
    private final RedisGuardrailService redisGuardrailService;
    private final NotificationService notificationService;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            BotRepository botRepository,
            RedisGuardrailService redisGuardrailService,
            NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.botRepository = botRepository;
        this.redisGuardrailService = redisGuardrailService;
        this.notificationService = notificationService;
    }

    // This method creates a comment and applies Redis guardrails for bot comments.
    public Comment createComment(Long postId, CreateCommentRequest request) {
        validateAuthorType(request.getAuthorType());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if ("BOT".equals(request.getAuthorType())) {
            return createBotComment(post, request);
        }
        return createHumanComment(post, request);
    }

    // This method handles bot comment flow with all required Redis checks.
    private Comment createBotComment(Post post, CreateCommentRequest request) {
        boolean botAllowed = redisGuardrailService.checkAndIncrementBotCount(post.getId());
        if (!botAllowed) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bot limit reached");
        }

        boolean depthAllowed = redisGuardrailService.checkDepthLevel(request.getDepthLevel());
        if (!depthAllowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Depth level cannot be more than 20");
        }

        boolean cooldownAllowed = redisGuardrailService.checkCooldown(request.getAuthorId(), post.getAuthorId());
        if (!cooldownAllowed) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Cooldown active");
        }

        Comment savedComment = saveComment(post.getId(), request);
        redisGuardrailService.incrementViralityScore(post.getId(), request.getAuthorType(), "BOT_REPLY");

        String botName = botRepository.findById(request.getAuthorId())
                .map(bot -> bot.getName())
                .orElse("Unknown Bot");
        notificationService.handleBotNotification(post.getAuthorId(), botName);
        return savedComment;
    }

    // This method handles human comments without guardrails and updates virality score.
    private Comment createHumanComment(Post post, CreateCommentRequest request) {
        Comment savedComment = saveComment(post.getId(), request);
        redisGuardrailService.incrementViralityScore(post.getId(), request.getAuthorType(), "HUMAN_COMMENT");
        return savedComment;
    }

    // This method saves a comment record to PostgreSQL.
    private Comment saveComment(Long postId, CreateCommentRequest request) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(request.getAuthorId());
        comment.setAuthorType(request.getAuthorType());
        comment.setContent(request.getContent());
        comment.setDepthLevel(request.getDepthLevel());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // This method validates that author type is only USER or BOT.
    private void validateAuthorType(String authorType) {
        if (!"USER".equals(authorType) && !"BOT".equals(authorType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorType must be USER or BOT");
        }
    }
}
