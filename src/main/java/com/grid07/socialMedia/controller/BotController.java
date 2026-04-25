package com.grid07.socialMedia.controller;

import com.grid07.socialMedia.dto.CreateBotRequest;
import com.grid07.socialMedia.entity.Bot;
import com.grid07.socialMedia.repository.BotRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    private final BotRepository botRepository;

    public BotController(BotRepository botRepository) {
        this.botRepository = botRepository;
    }

    // This method creates a new bot and saves it to PostgreSQL.
    @PostMapping
    public Bot createBot(@RequestBody CreateBotRequest request) {
        Bot bot = new Bot();
        bot.setName(request.getName());
        bot.setPersonaDescription(request.getPersonaDescription());
        return botRepository.save(bot);
    }
}
