package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Telegram предоставлять Rest API для интеграции с сервисом.
Для удобстава вызывов этих сервисов используется библиотека на Java.
Аннотация @Service говорит Spring, что этот класс является сервисом,
то есть его экземпляр должен быть создан и управляться контейнером Spring.
В конструкторе с аннотацией @Value происходит инъекция значений из конфигурационного файла application.properties.
 */

@Service
public class TgRemoteService extends TelegramLongPollingBot {

    private static final Map<String, String> MOOD_RESP = new HashMap<>();
    private final String botName;
    private final String botToken;
    private final UserRepository userRepository;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           UserRepository userRepository) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
    }

    /*
    getBotUsername() возвращает имя бота
     */
    @Override
    public String getBotUsername() {
        return botName;
    }

    /*
    getBotToken() возвращает токен, необходимый для взаимодействия с Telegram API
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /*
     Метод onUpdateReceived() обрабатывает данные, отправленные при нажатии кнопки, и отвечает пользователю подходящим сообщением.
     Если в событии есть информация о нажатии кнопки (CallbackQuery), бот извлекает данные кнопки и отправляет ответное сообщение.
     Далее из объекта CallbackQuery извлекаются данные кнопки (getData()), чтобы определить, какая кнопка была нажата.
     Идентификатор чата (chatId) извлекается из сообщения, чтобы отправить ответ обратно в тот же чат.
     Тексты ответов заранее заданы в коллекции MOOD_RESP.
     Добавил обработку команды /start, которая будет регистрировать пользователя в системе,
     сохраняя его данные
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            send(new SendMessage(String.valueOf(chatId), MOOD_RESP.get(data)));
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            if ("/start".equals(message.getText())) {
                long chatId = message.getChatId();
                var user = new User();
                user.setClientId(message.getFrom().getId());
                user.setChatId(chatId);
                userRepository.save(user);
                send(sendButtons(chatId));
            }

        }
    }

    /*
    Метод отправки сообщений.
     */
    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*
    Метод для создания кнопки.
     */
    InlineKeyboardButton createBtn(String name, String data) {
        var inline = new InlineKeyboardButton();
        inline.setText(name);
        inline.setCallbackData(data);
        return inline;
    }

    /*
    Метод для формирования сообщения, содержащего кнопки.
     */
    public SendMessage sendButtons(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Как настроение сегодня?");

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(List.of(createBtn("Потерял носок \uD83D\uDE22", "lost_sock")));
        keyboard.add(List.of(createBtn("Как огурец на полке \uD83D\uDE10", "cucumber")));
        keyboard.add(List.of(createBtn("Готов к танцам \uD83D\uDE04", "dance_ready")));
        keyboard.add(List.of(createBtn("Где мой кофе?! \uD83D\uDE23", "need_coffee")));
        keyboard.add(List.of(createBtn("Слипаются глаза \uD83D\uDE29", "sleepy")));

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return message;
    }

    static {
        MOOD_RESP.put("lost_sock", "Носки — это коварные создания. Но не волнуйся, второй обязательно найдётся!");
        MOOD_RESP.put("cucumber", "Огурец тоже дело серьёзное! Главное, не мариноваться слишком долго.");
        MOOD_RESP.put("dance_ready", "Супер! Танцуй, как будто никто не смотрит. Или, наоборот, как будто все смотрят!");
        MOOD_RESP.put("need_coffee", "Кофе уже в пути! Осталось только подождать... И ещё немного подождать...");
        MOOD_RESP.put("sleepy", "Пора на боковую! Даже супергерои отдыхают, ты не исключение.");
    }
}