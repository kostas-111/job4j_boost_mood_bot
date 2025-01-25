package ru.job4j.bmb.services;

/*
Сервис бизнес-логики
Класс, отвечающий за обработку запросов пользователя в зависимости от его настроения
 */

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.model.UserEvent;
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
public class MoodService implements BeanNameAware {
    private String beanName;
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

    public MoodService(ApplicationEventPublisher publisher, MoodRepository moodRepository, MoodLogRepository moodLogRepository,
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
        moodRepository.findById(moodId)
                .ifPresent(mood -> {
                    MoodLog log = new MoodLog();
                    publisher.publishEvent(new UserEvent(this, user));
                    moodLogRepository.save(log);
                });
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    /*
    Метод возвращает лог настроений пользователя за прошедшую неделю
     */
    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDay = LocalDateTime.now().minusDays(7);
        //LocalDateTime endDay = LocalDateTime.now();
        long startMillis = startDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        //long endMillis = endDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<MoodLog> weekLogs = moodLogService.findMoodLogsForWeek(clientId, startMillis);
        String logMessage = formatMoodLogs(weekLogs,  "Настроение пользователя за прошедшую неделю");
        var content = new Content(chatId);
        content.setText(logMessage);
        return Optional.of(content);
    }

    /*
    Метод возвращает лог настроений пользователя за прошедший месяц
     */
    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDay = LocalDateTime.now().minusMonths(1);
        //LocalDateTime endDay = LocalDateTime.now();
        long startMillis = startDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        //long endMillis = endDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<MoodLog> monthLogs = moodLogService.findMoodLogsForMonth(clientId, startMillis);
        String logMessage = formatMoodLogs(monthLogs,  "Настроение пользователя за прошедший месяц");
        var content = new Content(chatId);
        content.setText(logMessage);
        return Optional.of(content);
    }

    /*
    Метод возвращает список наград, которые пользователь получил за поддержание хорошего настроения
     */
    public Optional<Content> awards(long chatId, Long clientId) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Achievement> achievements = achievementRepository.findAllByUser(user);
        String awardMessage = "У вас отсутствуют награды за поддержание хорошего настроения";
        if (!achievements.isEmpty()) {
            awardMessage = achievements.stream()
                    .map(Achievement :: getAward)
                    .toString();
        }
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
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreateAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
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
