package ru.job4j.bmb.services;

/*
Компонент взаимодействия с Telegram API
Oсновной класс, использует Telegram API для получения и отправки сообщений
Класс описывает интеграцию с Telegram API
 */

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements BeanNameAware, SentContent {
    private final BotCommandHandler handler;
    private final String botName;
    private String beanName;

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
            handler.commands((Message) update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        long chatId = content.getChatId();
        String contentText = content.getText();
        InlineKeyboardMarkup contentMarkup = content.getMarkup();
            if (content.getAudio() != null) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(chatId);
                sendAudio.setAudio(content.getAudio());
                if (!contentText.isBlank()) {
                    sendAudio.setTitle(contentText);
                }
            } else if (!contentText.isBlank() && contentMarkup != null) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(contentText);
                message.setReplyMarkup(content.getMarkup());
                sendNewMessage(message);
            } else if (!contentText.isBlank() && contentMarkup == null && content.getAudio() == null && content.getPhoto() == null) {
                SendMessage message = new SendMessage();
                sendNewMessage(message);
            } else if (content.getPhoto() != null) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(content.getPhoto());
                if (!contentText.isBlank()) {
                    sendPhoto.setCaption(contentText);
                }
            }
    }

    private void sendNewMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void content(Content content) {
        handler.receive(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed now.");
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void printBeanName() {
        System.out.println("Bean name in context: " + beanName);
    }
}