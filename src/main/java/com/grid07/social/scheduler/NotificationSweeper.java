package com.grid07.social.scheduler;

import java.util.List;
import java.util.Set;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationSweeper {

    private final RedisTemplate<String, String> redisTemplate;

    public NotificationSweeper(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // This method sweeps pending notifications from Redis and logs one summary per user.
    @Scheduled(fixedRate = 300000)
    public void sweepPendingNotifications() {
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
            if (messages == null || messages.isEmpty()) {
                redisTemplate.delete(key);
                continue;
            }

            int count = messages.size();
            String firstMessage = messages.get(0);
            String botName = extractBotName(firstMessage);
            System.out.println(
                    "Summarized Push Notification: " + botName + " and " + (count - 1) + " others interacted with your posts.");
            redisTemplate.delete(key);
        }
    }

    // This method extracts bot name from a notification message.
    private String extractBotName(String message) {
        if (message == null) {
            return "Unknown Bot";
        }
        String prefix = "Bot ";
        String suffix = " replied to your post";
        if (message.startsWith(prefix) && message.endsWith(suffix)) {
            return message.substring(prefix.length(), message.length() - suffix.length());
        }
        return "Unknown Bot";
    }
}
