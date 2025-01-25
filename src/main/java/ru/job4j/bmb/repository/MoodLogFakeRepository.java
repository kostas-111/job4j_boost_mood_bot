package ru.job4j.bmb.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.MoodLog;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Profile("test")
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
}