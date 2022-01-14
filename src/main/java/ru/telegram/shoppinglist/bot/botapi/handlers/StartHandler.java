package ru.telegram.shoppinglist.bot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.telegram.shoppinglist.bot.botapi.BotState;
import ru.telegram.shoppinglist.bot.botapi.InputMessageHandler;
import ru.telegram.shoppinglist.bot.cache.UserDataCache;
import ru.telegram.shoppinglist.bot.service.ReplyMessageService;

@Component
public class StartHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;

    public StartHandler(UserDataCache userDataCache, ReplyMessageService replyMessageService) {
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.EMPTY_LIST;
    }

    private SendMessage processUsersInput(Message inputMessage) {
        long userId = inputMessage.getFrom().getId();
        String chatId = inputMessage.getChatId().toString();

        SendMessage replyToUser = replyMessageService.getReplyMessage(chatId, "reply.start");
        userDataCache.setUserCurrentBotState(userId, BotState.DATA_INPUT);
        return replyToUser;
    }
}
