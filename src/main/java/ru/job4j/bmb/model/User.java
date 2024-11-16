package ru.job4j.bmb.model;

import java.util.Objects;

public class User {
    private long id;
    private long clientId;
    private long chatId;

    public User(long id, long clientId, long chatId) {
        this.id = id;
        this.clientId = clientId;
        this.chatId = chatId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && clientId == user.clientId && chatId == user.chatId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, chatId);
    }
}