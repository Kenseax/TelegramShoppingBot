package ru.telegram.shoppinglist.bot.botapi.handlers;

import it.rebase.rebot.api.emojis.Emoji;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.telegram.shoppinglist.bot.botapi.BotState;
import ru.telegram.shoppinglist.bot.botapi.InputMessageHandler;
import ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles.UserProfileData;
import ru.telegram.shoppinglist.bot.cache.UserDataCache;
import ru.telegram.shoppinglist.bot.service.MainMenuService;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInputHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;

    public DataInputHandler(UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.DATA_INPUT;
    }

    private SendMessage processUsersInput(Message inputMessage) {
        List<String> list;
        String goodsToBuy = inputMessage.getText();
        long userId = inputMessage.getFrom().getId();
        String chatId = inputMessage.getChatId().toString();

        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUserCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.EMPTY_LIST)) {
            replyToUser = mainMenuService.getMainMenuMessage(chatId,"Начните вводить продукты");
            userDataCache.setUserCurrentBotState(userId, BotState.DATA_INPUT);
        }

        if (botState.equals(BotState.DATA_INPUT)) {
            list = userProfileData.getListOfGoods();
            list.add(goodsToBuy + " " + Emoji.CROSS_MARK);
            userProfileData.setListOfGoods(list);

            replyToUser = new SendMessage(chatId, "Список покупок: \n");
            replyToUser.setReplyMarkup(getButtonsMarkup(userProfileData.getListOfGoods()));
        }

        if (botState.equals(BotState.CLEAR_LIST)) {
            list = userProfileData.getListOfGoods();
            list.clear();
            userProfileData.setListOfGoods(list);
            replyToUser = new SendMessage(chatId, "Список покупок очищен");
            userDataCache.setUserCurrentBotState(userId, BotState.DATA_INPUT);
        }

        if (botState.equals(BotState.DELETE_LAST)) {
            list = userProfileData.getListOfGoods();
            if (!list.isEmpty()) {
                list.remove(list.size() - 1);
                userProfileData.setListOfGoods(list);
                replyToUser = new SendMessage(chatId, "Последняя позиция удалена");
            } else {
                replyToUser = new SendMessage(chatId, "Список покупок пуст");
            }
            userDataCache.setUserCurrentBotState(userId, BotState.DATA_INPUT);
        }

        userDataCache.saveUserProfileData(userId, userProfileData);
        return replyToUser;
    }

    public InlineKeyboardMarkup getButtonsMarkup(List<String> textMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton;
        List<InlineKeyboardButton> keyboardButtonsRow;
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (String s : textMessage) {
            inlineKeyboardButton = new InlineKeyboardButton();
            keyboardButtonsRow = new ArrayList<>();

            inlineKeyboardButton.setText(s);
            inlineKeyboardButton.setCallbackData("button_" + s);
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
