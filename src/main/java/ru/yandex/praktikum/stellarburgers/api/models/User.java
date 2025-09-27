package ru.yandex.praktikum.stellarburgers.api.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String email;
    private String name;
    private String password;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
    }
}