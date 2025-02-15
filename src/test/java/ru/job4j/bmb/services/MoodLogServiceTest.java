package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodFakeRepository;
import ru.job4j.bmb.repository.MoodLogFakeRepository;
import ru.job4j.bmb.repository.MoodRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {MoodLogService.class, MoodLogFakeRepository.class})
class MoodLogServiceTest {

    @Autowired
    private MoodLogService moodLogService;

    @Autowired
    @Qualifier("moodLogFakeRepository")
    private MoodLogFakeRepository moodLogFakeRepository;

    @Test
    void whenFindMoodLogsForWeek() {
        var dayForFirstLogAndStartWeek = LocalDate
                .now()
                .minusWeeks(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var dayForSecondLog = LocalDate
                .now()
                .minusWeeks(2)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var dayForThirdLog = dayForFirstLogAndStartWeek + 100;
        User user = new User();
        user.setId(1L);
        Mood mood = new Mood("Счастливейший на свете \uD83D\uDE0E", true);
        mood.setId(1L);
        MoodLog log1 = new MoodLog(user, mood, dayForFirstLogAndStartWeek);
        log1.setId(1L);
        MoodLog log2 = new MoodLog(user, mood, dayForSecondLog);
        log2.setId(2L);
        MoodLog log3 = new MoodLog(user, mood, dayForThirdLog);
        log3.setId(3L);
        moodLogFakeRepository.saveAll(Arrays.asList(log1, log2, log3));
        List<MoodLog> result = moodLogService.findMoodLogsForWeek(user.getId(), dayForFirstLogAndStartWeek);
        assertThat(result).containsOnly(log1, log3);
    }
}