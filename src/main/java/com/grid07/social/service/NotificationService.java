package com.grid07.social.service;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final RedisTemplate<String, String> redisTemplate;

    public NotificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // This method sends a notification now or queues it when user notification cooldown is active.
    public void handleBotNotification(Long userId, String botName) {
        String cooldownKey = "notif:cooldown:user_" + userId;
        String listKey = "user:" + userId + ":pending_notifs";
        String message = "Bot " + botName + " replied to your post";

        Boolean exists = redisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.opsForList().rightPush(listKey, message);
            return;
        }

        System.out.println("Push Notification Sent to User " + userId);
        redisTemplate.opsForValue().set(cooldownKey, "1", 15, TimeUnit.MINUTES);
    }
}
