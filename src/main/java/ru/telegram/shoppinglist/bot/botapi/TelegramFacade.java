package ru.telegram.shoppinglist.bot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegram.shoppinglist.bot.botapi.handlers.CallbackQueryHandler;
import ru.telegram.shoppinglist.bot.cache.UserDataCache;

@Component
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private CallbackQueryHandler callbackQueryHandler;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache,
                          CallbackQueryHandler callbackQueryHandler) { // Присваевание переменных
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.callbackQueryHandler = callbackQueryHandler;
    }

    public BotApiMethod<?> handleUpdate(Update update) { // Обработчик запросов
        SendMessage replyMessage = null;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        }
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMessage) {
            case "/start":
            case "Начать":
                botState = BotState.EMPTY_LIST;
                break;
            case "Очистить список":
                botState = BotState.CLEAR_LIST;
                break;
            case "Удалить последнюю позицию":
                botState = BotState.DELETE_LAST;
                break;
            default:
                botState = userDataCache.getUserCurrentBotState(userId);
                break;
        }
        userDataCache.setUserCurrentBotState(userId, botState);
        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }
}
