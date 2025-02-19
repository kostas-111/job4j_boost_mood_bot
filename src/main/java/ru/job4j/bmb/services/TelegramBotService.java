package ru.job4j.bmb.services;

/*
Компонент взаимодействия с Telegram API
Oсновной класс, использует Telegram API для получения и отправки сообщений
Класс описывает интеграцию с Telegram API
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.config.DevCondition;
import ru.job4j.bmb.content.Content;

@Conditional(DevCondition.class)
@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        String chatId = String.valueOf(content.getChatId());
        String contentText = content.getText();
        InlineKeyboardMarkup contentMarkup = content.getMarkup();
            if (content.getAudio() != null) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(chatId);
                sendAudio.setAudio(content.getAudio());
                if (!content.getText().isBlank()) {
                    sendAudio.setTitle(contentText);
                }
            }
            if (content.getPhoto() != null) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(content.getPhoto());
                if (!content.getText().isBlank()) {
                    sendPhoto.setCaption(contentText);
                }
            }
            if (!content.getText().isBlank() && contentMarkup != null) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(content.getText());
                message.setReplyMarkup(content.getMarkup());
                sendNewMessage(message);
            } else if (!contentText.isBlank() && contentMarkup == null && content.getAudio() == null && content.getPhoto() == null) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(content.getText());
                sendNewMessage(message);
            }
    }

    private void sendNewMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}