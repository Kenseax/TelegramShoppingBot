package ru.telegram.shoppinglist.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.telegram.shoppinglist.bot.ShoppingListBot;
import ru.telegram.shoppinglist.bot.botapi.TelegramFacade;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webhookPath;
    private String botUsername;
    private String botToken;

    @Bean
    public ShoppingListBot shoppingListBot(TelegramFacade telegramFacade) {
        ShoppingListBot bot = new ShoppingListBot(telegramFacade);
        bot.setWebhookPath(webhookPath);
        bot.setBotUsername(botUsername);
        bot.setBotToken(botToken);

        return bot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }
}
