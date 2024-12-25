package ru.job4j.bmb.repository;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoodLogFakeRepository
        extends CrudRepositoryFake<MoodLog, Long>
        implements MoodLogRepository {

    public List<MoodLog> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<MoodLog> findByUserAndCreateAtBetween(User user, long startDay, long endDay) {
        return null;
    }

    @Override
    public List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getCreateAt() < startOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .collect(Collectors.toList());
    }
}