package ru.job4j.bmb.services;

/*
Сервис бизнес-логики
Класс, который следит за достижениями пользователя и награждает его за выполнение определенных действий
 */

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.UserEvent;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {

    private final SentContent sentContent;
    private final AwardRepository awardRepository;
    private final MoodLogRepository moodLogRepository;

    public AchievementService(SentContent sentContent,
                              AwardRepository awardRepository,
                              MoodLogRepository moodLogRepository) {
        this.sentContent = sentContent;
        this.awardRepository = awardRepository;
        this.moodLogRepository = moodLogRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
    }
}