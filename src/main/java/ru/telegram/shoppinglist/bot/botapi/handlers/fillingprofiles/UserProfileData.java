package ru.telegram.shoppinglist.bot.botapi.handlers.fillingprofiles;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class UserProfileData {
    private List<String> listOfGoods = new LinkedList<>();
    private String goods;

    @Override
    public String toString() {
        if (listOfGoods.isEmpty()) {
            return "Пусто";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : listOfGoods) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }
}
