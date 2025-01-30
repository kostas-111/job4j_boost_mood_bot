package ru.job4j.bmb.services;

/*
Сервис бизнес-логики
Класс, отвечающий за обработку запросов пользователя в зависимости от его настроения
 */

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final ApplicationEventPublisher publisher;
    private final MoodRepository moodRepository;
    private final MoodLogRepository moodLogRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final RecommendationEngine recommendationEngine;
    private final MoodLogService moodLogService;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(ApplicationEventPublisher publisher,
                       MoodRepository moodRepository,
                       MoodLogRepository moodLogRepository,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       RecommendationEngine recommendationEngine,
                       MoodLogService moodLogService) {
        this.publisher = publisher;
        this.moodRepository = moodRepository;
        this.moodLogRepository = moodLogRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.recommendationEngine = recommendationEngine;
        this.moodLogService = moodLogService;
    }

    /*
    Метод позволяет пользователю выбрать текущее настроение и фиксирует этот выбор в логе событий
     */
    public Content chooseMood(User user, Long moodId) {
        LocalDateTime startDay = LocalDateTime.now();
        long startMillis = startDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        moodRepository.findById(moodId)
                .ifPresent(mood -> {
                    MoodLog log = new MoodLog(user, mood, startMillis);
                    moodLogRepository.save(log);
                    publisher.publishEvent(new UserEvent(this, user));
                });
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    /*
    Метод возвращает лог настроений пользователя за прошедшую неделю
     */
    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        User user = userRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDay = LocalDateTime.now().minusDays(7);
        long startMillis = startDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<MoodLog> weekLogs = moodLogService.findMoodLogsForWeek(user.getId(), startMillis);
        String logMessage = formatMoodLogs(weekLogs,  "Настроение пользователя за прошедшую неделю");
        var content = new Content(chatId);
        content.setText(logMessage);
        return Optional.of(content);
    }

    /*
    Метод возвращает лог настроений пользователя за прошедший месяц
     */
    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        User user = userRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDay = LocalDateTime.now().minusMonths(1);
        long startMillis = startDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<MoodLog> monthLogs = moodLogService.findMoodLogsForMonth(user.getId(), startMillis);
        String logMessage = formatMoodLogs(monthLogs,  "Настроение пользователя за прошедший месяц");
        var content = new Content(chatId);
        content.setText(logMessage);
        return Optional.of(content);
    }

    /*
    Метод возвращает список наград, которые пользователь получил за поддержание хорошего настроения
     */
    public Optional<Content> awards(long chatId, Long clientId) {
        User user = userRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Achievement> achievements = achievementRepository.findAllByUser(user);
        String awardMessage = formatAchievement(achievements, "Награды пользователя за его достижения");
        var content = new Content(chatId);
        content.setText(awardMessage);
        return Optional.of(content);
    }

    /*
    Метод форматирования строки лога события, добавляющий к логу дату
     */
    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ": \nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochMilli(log.getCreateAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    private String formatAchievement(List<Achievement> achievements, String title) {
        if (achievements.isEmpty()) {
            return title + ": \nУ вас отсутствуют награды за поддержание хорошего настроения.";
        }
        var sb = new StringBuilder(title + ":\n");
        achievements.forEach(achievement -> sb.append("Количество дней хорошего настроения: ")
                .append(achievement.getAward().getDays()).append(". ")
                .append(achievement.getAward().getTitle()).append(". ")
                .append(achievement.getAward().getDescription()).append("\n"));
        return sb.toString();
    }
}
