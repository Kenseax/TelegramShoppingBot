package ru.telegram.shoppinglist.bot;

import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegram.shoppinglist.bot.botapi.TelegramFacade;

@Setter
public class ShoppingListBot extends TelegramWebhookBot {
    private String webhookPath;
    private String botUsername;
    private String botToken;
    private TelegramFacade telegramFacade;

    public ShoppingListBot(TelegramFacade telegramFacade) {
        this.telegramFacade = telegramFacade;
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }
}
