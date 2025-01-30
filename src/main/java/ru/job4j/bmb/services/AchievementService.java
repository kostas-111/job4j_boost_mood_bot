package ru.job4j.bmb.services;

/*
Сервис бизнес-логики
Класс, который следит за достижениями пользователя и награждает его за выполнение определенных действий
 */

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.model.UserEvent;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {

    private final SentContent sentContent;
    private final AwardRepository awardRepository;
    private final MoodLogRepository moodLogRepository;
    private final AchievementRepository achievementRepository;

    public AchievementService(SentContent sentContent,
                              AwardRepository awardRepository,
                              MoodLogRepository moodLogRepository,
                              AchievementRepository achievementRepository) {
        this.sentContent = sentContent;
        this.awardRepository = awardRepository;
        this.moodLogRepository = moodLogRepository;
        this.achievementRepository = achievementRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        User user = event.getUser();
        LocalDateTime startDay = LocalDateTime.now();
        long startMillis = startDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        int goodDays = moodLogRepository.findByUserId(user.getId()).stream()
                .map(moodLog -> moodLog.getMood().isGood() ? 1 : 0)
                .reduce(0, (streak, value) -> value == 1 ? streak + 1 : 0);
        awardRepository.findAll().stream()
                .filter(award -> award.getDays() == goodDays)
                .findFirst()
                .ifPresent(award -> {
                    achievementRepository.save(new Achievement(startMillis, user, award));
                    Content content = new Content(user.getChatId());
                    content.setText("Поздравляем с получением достижения: "
                            + award.getTitle() + System.lineSeparator()
                            + award.getDescription());
                    sentContent.sent(content);
                });
    }
}