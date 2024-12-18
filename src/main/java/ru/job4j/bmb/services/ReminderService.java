package ru.job4j.bmb.services;

/*
Сервис бизнес-логики
Класс, который управляет ежедневными напоминаниями и уведомлениями
 */

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

@Service
public class ReminderService implements BeanNameAware {

    private String beanName;
    private final TelegramBotService tgBotService;
    private final UserRepository userRepository;

    public ReminderService(TelegramBotService tgBotService, UserRepository userRepository) {
        this.tgBotService = tgBotService;
        this.userRepository = userRepository;
    }

    /*
     Метод отправляет сообщения "Ping" через интервал, заданный в application.properties
     */
    @Scheduled(fixedRateString = "${remind.period}")
    public void ping() {
        var user = userRepository.findAll().stream()
                .reduce((first, last) -> last);
        if (user.isPresent()) {
            Content message = new Content(user.get().getChatId());
            message.setText("Ping");
            tgBotService.sent(message);
        }
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