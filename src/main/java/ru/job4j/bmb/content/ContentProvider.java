package ru.job4j.bmb.content;

/*
Система рекомендаций
Интерфейс, который определяет методы для получения контента (например, цитат, музыки, видео).
Реализации этого интерфейса могут обращаться к различным внешним API или использовать локальные данные
 */

public interface ContentProvider {
    Content byMood(Long chatId, Long moodId);
}