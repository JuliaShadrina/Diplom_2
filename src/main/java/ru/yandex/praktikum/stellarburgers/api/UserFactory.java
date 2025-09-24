package ru.yandex.praktikum.stellarburgers.api;

import com.github.javafaker.Faker;

public class UserFactory {
    private static final Faker faker = new Faker();

    public static String userEmailGenerator() {
        return faker.internet().emailAddress();
    }

    public static String userNameGenerator() {
        return faker.name().fullName();
    }

    public static String userPasswordGenerator() {
        return faker.internet().password();
    }

}
