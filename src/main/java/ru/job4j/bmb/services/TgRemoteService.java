package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken) {
        this.botName = botName;
        this.botToken = botToken;
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
     Метод onUpdateReceived() вызывается каждый раз, когда бот получает обновление (например, новое сообщение).
     В блоке if проверяется, содержит ли обновление сообщение и текст. Если да, продолжается дальнейшая обработка.
     messageText и chatId - здесь извлекается текст, который отправил пользователь, и идентификатор чата, чтобы бот знал, куда отправлять ответ.
     Далее создаётся объект SendMessage, который будет отправлен обратно пользователю. В поле text задаётся сообщение,
     содержащее текст: "Вы написали: [сообщение пользователя]".
     Метод execute() отправляет сообщение через Telegram API. Если во время отправки произойдёт ошибка, она будет обработана в блоке catch,
     и исключение будет выведено в консоль.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Вы написали: " + messageText);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }}