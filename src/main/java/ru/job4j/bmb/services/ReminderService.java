package ru.job4j.bmb.services;

/*
Сервис бизнес-логики
Класс, который управляет ежедневными напоминаниями и уведомлениями
 */

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.MoodLogRepository;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ReminderService {

    private final SentContent sentContent;
    private final MoodLogRepository moodLogRepository;
    private final TgUI tgUI;
    private final MoodLogService moodLogService;

    public ReminderService(SentContent sentContent,
                           MoodLogRepository moodLogRepository,
                           TgUI tgUI,
                           MoodLogService moodLogService) {
        this.sentContent = sentContent;
        this.moodLogRepository = moodLogRepository;
        this.tgUI = tgUI;
        this.moodLogService = moodLogService;
    }

    /*
    Метод отправляет сообщение с предложением оценить настроение ежедневно.
    Если пользователь уже выбрал настроение за текущий день, сообщение отправлять не нужно.
    */
    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void remindUsers() {
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        for (var user : moodLogService.findUserWhoDidNotVoteToday(startOfDay)) {
            var content = new Content(user.getChatId());
            content.setText("Как настроение?");
            content.setMarkup(tgUI.buildButtons());
            sentContent.sent(content);
        }
    }
}