package ru.telegram.shoppinglist.bot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.telegram.shoppinglist.bot.ShoppingListBot;

@RestController
public class WebHookController {
    private final ShoppingListBot shoppingListBot;

    public WebHookController(ShoppingListBot shoppingListBot) {
        this.shoppingListBot = shoppingListBot;
    }

    @PostMapping(value = "/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return shoppingListBot.onWebhookUpdateReceived(update);
    }
}
