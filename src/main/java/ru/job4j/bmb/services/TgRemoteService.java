package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;

/*
Telegram предоставлять Rest API для интеграции с сервисом.
Для удобстава вызывов этих сервисов используется библиотека на Java.
Аннотация @Service говорит Spring, что этот класс является сервисом,
то есть его экземпляр должен быть создан и управляться контейнером Spring.
В конструкторе с аннотацией @Value происходит инъекция значений из конфигурационного файла application.properties.
 */

@Service
public class TgRemoteService extends TelegramLongPollingBot {
    private final String botName;
    private final String botToken;
    private final UserRepository userRepository;
    private final MoodRepository moodRepository;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           UserRepository userRepository,
                           MoodRepository moodRepository) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.moodRepository = moodRepository;
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
            var data = update.getCallbackQuery().getId();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            send(new SendMessage(String.valueOf(chatId), moodRepository.findById(Long.valueOf(data)).toString()));
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
    Метод для отправки сообщения, содержащего кнопки.
     */
    public SendMessage sendButtons(long chatId) {
        SendMessage message = new SendMessage();
        TgUI button = new TgUI(moodRepository);
        message.setChatId(chatId);
        message.setText("Как настроение сегодня?");
        message.setReplyMarkup(button.buildButtons());
        return message;
    }
}