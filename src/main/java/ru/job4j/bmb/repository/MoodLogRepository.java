package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import java.util.List;

/*
Компонент хранения данных
Хранит журнал настроения пользователей
 */

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);
}