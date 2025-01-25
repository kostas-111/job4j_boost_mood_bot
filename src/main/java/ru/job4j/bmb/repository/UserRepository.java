package ru.job4j.bmb.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import java.util.List;

/*
Компонент хранения данных
Класс, который отвечает за хранение данных о пользователях
 */

@Profile("prod")
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    User findByClientId(Long clientId);
}