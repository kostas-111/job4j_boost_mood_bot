package ru.job4j.bmb.repository;

import ru.job4j.bmb.model.User;
import java.util.List;

/*
Компонент хранения данных
Класс, который отвечает за хранение данных о пользователях
 */

public interface UserRepository {
    List<User> findAll();

    User findByClientId(Long clientId);
}