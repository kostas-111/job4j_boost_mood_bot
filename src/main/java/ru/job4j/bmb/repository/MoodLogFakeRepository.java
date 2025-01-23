package ru.job4j.bmb.repository;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoodLogFakeRepository
        extends CrudRepositoryFake<MoodLog, Long>
        implements MoodLogRepository {

    public List<MoodLog> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<MoodLog> findByUserId(Long userId) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Stream<MoodLog> findByUserIdOrderByCreateAtDesc(Long userId) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(MoodLog::getCreateAt).reversed());
    }

    @Override
    public List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreateAt() >= weekStart)
                .collect(Collectors.toList());
    }

    @Override
    public List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreateAt() >= monthStart)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUserWhoDidNotVoteToday(long startOfDay) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getCreateAt() < startOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<MoodLog> findByUsersAndCreateAtBetween(User user, long startDay, long endDay) {
        return null;
    }
}