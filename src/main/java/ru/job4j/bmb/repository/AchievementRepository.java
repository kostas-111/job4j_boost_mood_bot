package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.Achievement;

import java.util.List;

/*
Компонент хранения данных
Класс для хранения данных о достижениях пользователя
 */

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findAll();
}
