package ru.job4j.bmb.services;

/*
Компонент взаимодействия с Telegram API
Класс для обработки команд, поступающих от пользователей
 */

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

@Service
public class BotCommandHandler implements BeanNameAware {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;
    private String beanName;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
    }

    /*
    Метод commands обрабатывает входящие текстовые сообщения от пользователя
    и выполняет соответствующие действия на основе команды, содержащейся в сообщении.
    Он возвращает объект Optional<Content>, который содержит результат
    выполнения команды, либо пустое значение, если команда не распознана
     */
    Optional<Content> commands(Message message) {
        var user = new User();
        long userId = Long.parseLong(userRepository.findById(user.getId()).toString());
        long chatId = Long.parseLong(userRepository.findById(user.getChatId()).toString());
        String userMessage = message.getFormattedMessage();
        Optional<Content> content = Optional.empty();
        if ("/start".equals(userMessage)) {
            content = handleStartCommand(user.getChatId(), user.getClientId());
        }
        if ("/week_mood_log".equals(userMessage)) {
            content = moodService.weekMoodLogCommand(chatId, userId);
        }
        if ("/month_mood_log".equals(userMessage)) {
            content = moodService.monthMoodLogCommand(chatId, userId);
        }
        if ("/award".equals(userMessage)) {
            content = moodService.awards(chatId, userId);
        }
        return content;
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findById(callback.getFrom().getId());
        return user.map(value -> moodService.chooseMood(value, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        userRepository.save(user);
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    void receive(Content content) {
        System.out.println(content);
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