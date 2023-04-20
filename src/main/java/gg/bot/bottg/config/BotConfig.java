package gg.bot.bottg.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static java.lang.System.getProperty;

@Configuration
public class BotConfig {


    @Bean
    public TelegramBot getTelegramBot() {
        return new TelegramBot("5834417527:AAG8gchW6DfhHswxWv1jJexhuLP8IT6yfdk");
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
