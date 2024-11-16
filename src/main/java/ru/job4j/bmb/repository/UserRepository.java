package ru.job4j.bmb.repository;

/*
Компонент хранения данных
Класс, который отвечает за хранение данных о пользователях
 */

import ru.job4j.bmb.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findByClientid(Long clientId);
}
