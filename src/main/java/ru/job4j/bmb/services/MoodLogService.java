package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MoodLogService {
    private final MoodLogRepository moodLogRepository;

    public MoodLogService(MoodLogRepository moodLogRepository) {
        this.moodLogRepository = moodLogRepository;
    }

    public Stream<MoodLog> findByUserIdOrderByCreateAtDesc(Long userId) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(MoodLog::getCreateAt).reversed());
    }

    public List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreateAt() >= weekStart)
                .collect(Collectors.toList());
    }

    public List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreateAt() >= monthStart)
                .collect(Collectors.toList());
    }

    public List<User> findUserWhoDidNotVoteToday(long startOfDay) {
        return  moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getCreateAt() < startOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .collect(Collectors.toList());
    }
}