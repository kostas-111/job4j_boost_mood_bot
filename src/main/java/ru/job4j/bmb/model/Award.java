package ru.job4j.bmb.model;

import jakarta.persistence.*;
import java.util.Objects;

/*
Награды, доступные в системе
 */

@Entity
@Table(name = "mb_award")
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private int days;

    public Award(String title, String description, int days) {
        this.title = title;
        this.description = description;
        this.days = days;
    }

    public Award() {

    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDays() {
        return days;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDays(int days) {
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Award award = (Award) o;
        return Objects.equals(id, award.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
