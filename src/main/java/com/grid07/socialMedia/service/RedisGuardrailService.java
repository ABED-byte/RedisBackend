package com.grid07.socialMedia.service;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisGuardrailService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisGuardrailService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // This method increases a post virality score based on interaction type.
    public void incrementViralityScore(Long postId, String authorType, String interactionType) {
        String key = "post:" + postId + ":virality_score";
        long points = 0;

        if ("BOT_REPLY".equals(interactionType)) {
            points = 1;
        } else if ("HUMAN_LIKE".equals(interactionType)) {
            points = 20;
        } else if ("HUMAN_COMMENT".equals(interactionType)) {
            points = 50;
        }

        if (points > 0) {
            redisTemplate.opsForValue().increment(key, points);
        }
    }

    // This method atomically increments bot reply count for a post and validates the max limit.
    public boolean checkAndIncrementBotCount(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long newCount = redisTemplate.opsForValue().increment(key);
        return newCount != null && newCount <= 100;
    }

    // This method checks whether the comment depth level is allowed.
    public boolean checkDepthLevel(int depthLevel) {
        return depthLevel <= 20;
    }

    // This method blocks repeated bot-to-human interactions for 10 minutes.
    public boolean checkCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        Boolean exists = redisTemplate.hasKey(key);

        if (Boolean.TRUE.equals(exists)) {
            return false;
        }

        redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
        return true;
    }
}
