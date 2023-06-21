package ru.telegram.shoppinglist.bot.cache;

import org.springframework.stereotype.Component;
import ru.telegram.shoppinglist.bot.botapi.BotState;
import ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles.UserProfileData;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotState = new HashMap<>();
    private final Map<Long, UserProfileData> usersProfileData = new HashMap<>();

    @Override
    public void setUserCurrentBotState(long userId, BotState botState) {
        usersBotState.put(userId, botState);
    }

    /** Получение состояния бота */
    @Override
    public BotState getUserCurrentBotState(long userId) {
        BotState botState = usersBotState.get(userId);
        if (botState == null) {
            botState = BotState.EMPTY_LIST;
        }
        return botState;
    }

    /** Получение данных о профиле */
    @Override
    public UserProfileData getUserProfileData(long userId) {
        UserProfileData userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
        }
        return userProfileData;
    }

    @Override
    public void saveUserProfileData(long userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }
}
