package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.Mood;
import java.util.List;

/*
Компонент хранения данных
Класс, который отвечает за хранение данных о типах настроений
 */

@Repository
public interface MoodRepository extends CrudRepository<Mood, Long> {
    List<Mood> findAll();
}
