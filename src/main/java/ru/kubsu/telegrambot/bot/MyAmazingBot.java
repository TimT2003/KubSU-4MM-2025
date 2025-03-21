package ru.kubsu.telegrambot.bot;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.kubsu.telegrambot.ai.deepseek.DeepSeekService;

import java.util.ArrayList;
import java.util.List;

public final class MyAmazingBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final DeepSeekService deepSeekService;

    public MyAmazingBot(final TelegramClient telegramClient,
                        DeepSeekService deepSeekService) {
        this.telegramClient = telegramClient;
        this.deepSeekService = deepSeekService;
    }

    @Override
    public void consume(final Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            final String messageText = update.getMessage().getText();
            System.out.println("RECEIVE: " + messageText);

            try {
                if (messageText.equals("/start")) {
                    final SendMessage sendMessage = new SendMessage(
                            update.getMessage().getChatId().toString(),
                            "\uD83D\uDE07");

                    // Создаем клавиатуру
                    final ReplyKeyboardMarkup keyboardMarkup = createKeyboard();
                    sendMessage.setReplyMarkup(keyboardMarkup);

                    telegramClient.execute(sendMessage);
                } else if (messageText.equals("/image")) {
                    // Отправляем изображение
                    final SendPhoto sendPhoto = new SendPhoto(
                            update.getMessage().getChatId().toString(),
                            new InputFile("https://i.ytimg.com/vi/niPbG6vKJFU/maxresdefault.jpg")
                    );

                    telegramClient.execute(sendPhoto);
                } else {
                    final String aiAnswer = deepSeekService.ask(messageText);

                    final SendMessage sendMessage = new SendMessage(
                            update.getMessage().getChatId().toString(),
                            aiAnswer);
                    telegramClient.execute(sendMessage);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private ReplyKeyboardMarkup createKeyboard() {
        // Создаем строки для клавиатуры
        final List<KeyboardRow> keyboard = new ArrayList<>();

        final ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        // Первая строка
        final KeyboardRow row1 = new KeyboardRow();
        row1.add("Кто такой c++?");
        row1.add("Where UFO?");
        keyboard.add(row1);

        // Вторая строка
        final KeyboardRow row2 = new KeyboardRow();
        row2.add("/image");
        row2.add("Анекдот и смайлик клоуна в конце!");
        keyboard.add(row2);

        final KeyboardRow row3 = new KeyboardRow();
        row3.add("Самая большая звезда?");
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}