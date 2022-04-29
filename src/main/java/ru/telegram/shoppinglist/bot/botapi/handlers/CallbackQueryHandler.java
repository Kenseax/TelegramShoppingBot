package ru.telegram.shoppinglist.bot.botapi.handlers;

import it.rebase.rebot.api.emojis.Emoji;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles.UserProfileData;
import ru.telegram.shoppinglist.bot.cache.UserDataCache;

import java.util.List;

@Component
public class CallbackQueryHandler {
    private UserDataCache userDataCache;
    private DataInputHandler dataInputHandler;

    public CallbackQueryHandler(UserDataCache userDataCache, DataInputHandler dataInputHandler) {
        this.userDataCache = userDataCache;
        this.dataInputHandler = dataInputHandler;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final String chatId = buttonQuery.getMessage().getChatId().toString();
        final long userId = buttonQuery.getFrom().getId();
        String message = buttonQuery.getData().substring(7);
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        List<String> list = userProfileData.getListOfGoods();
        BotApiMethod<?> reply;

        if (list != null & list.contains(message) &
                !message.substring(message.length() - 1).equals(Emoji.WHITE_HEAVY_CHECK_MARK.toString())) {

            list.remove(message);
            list.add(0, message.substring(0, message.length() - 2) + " " + Emoji.WHITE_HEAVY_CHECK_MARK);
            userProfileData.setListOfGoods(list);
            SendMessage replyToUser = new SendMessage(chatId, "Список покупок:");
            replyToUser.setReplyMarkup(dataInputHandler.getButtonsMarkup(userProfileData.getListOfGoods()));
            return replyToUser;
        } else {
            reply = sendAnswerCallbackQuery("Уже отмечено", false, buttonQuery);
        }
        return reply;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
