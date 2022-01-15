package ru.telegram.shoppinglist.bot.botapi;

import it.rebase.rebot.api.emojis.Emoji;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegram.shoppinglist.bot.botapi.handlers.DataInputHandler;
import ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles.UserProfileData;
import ru.telegram.shoppinglist.bot.cache.UserDataCache;

import java.util.List;

@Component
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private DataInputHandler dataInputHandler;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache,
                          DataInputHandler dataInputHandler) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.dataInputHandler = dataInputHandler;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return processCallbackQuery(callbackQuery);
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

    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final String chatId = buttonQuery.getMessage().getChatId().toString();
        final long userId = buttonQuery.getFrom().getId();
        String message = buttonQuery.getData().substring(7);
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);

        List<String> list = userProfileData.getListOfGoods();
        list.remove(message);
        list.add(message.substring(0, message.length() - 2) + " " + Emoji.WHITE_HEAVY_CHECK_MARK);
        userProfileData.setListOfGoods(list);

        SendMessage replyToUser = new SendMessage(chatId, "Список покупок:");
        replyToUser.setReplyMarkup(dataInputHandler.getButtonsMarkup(userProfileData.getListOfGoods()));
        userDataCache.setUserCurrentBotState(userId, BotState.DATA_INPUT);
        return replyToUser;
    }
}
