package ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles;

import lombok.Data;
import java.util.LinkedList;
import java.util.List;

@Data
public class UserProfileData {
    private List<String> listOfGoods = new LinkedList<>();
}
