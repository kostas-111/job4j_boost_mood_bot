package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import java.util.List;
import java.util.stream.Stream;

/*
Компонент хранения данных
Хранит журнал настроения пользователей
 */

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);

    Stream<MoodLog> findByUserIdOrderByCreateAtDesc(Long userId);

    List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart);

    List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart);

    List<User> findUserWhoDidNotVoteToday(long startOfDay);

    List<MoodLog> findByUsersAndCreateAtBetween(User user, long startDay, long endDay);
}
