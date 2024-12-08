package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.MoodContent;
import java.util.List;

/*
Компонент хранения данных
Класс хранения oтветов для пользователя
 в зависимости от настроения
 */

public interface MoodContentRepository extends CrudRepository<MoodContent, Long> {
    List<MoodContent> findAll();
}
