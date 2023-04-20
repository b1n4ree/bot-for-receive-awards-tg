package gg.bot.bottg.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static java.lang.System.getProperty;

@Configuration
public class BotConfig {

    @Value("${bot_token}")
    private String botToken;

    @Bean
    public TelegramBot getTelegramBot() {
        return new TelegramBot(botToken);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
