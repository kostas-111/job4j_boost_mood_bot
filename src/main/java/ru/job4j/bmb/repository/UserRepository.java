package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.User;
import java.util.List;

/*
Компонент хранения данных
Класс, который отвечает за хранение данных о пользователях
 */

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
}