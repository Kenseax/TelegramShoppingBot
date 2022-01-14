package ru.telegram.shoppinglist.bot.cache;

import ru.telegram.shoppinglist.bot.botapi.BotState;
import ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles.UserProfileData;

public interface DataCache {
    void setUserCurrentBotState(long userId, BotState botState);

    BotState getUserCurrentBotState(long userId);

    UserProfileData getUserProfileData(long userId);

    void saveUserProfileData(long userId, UserProfileData userProfileData);
}
