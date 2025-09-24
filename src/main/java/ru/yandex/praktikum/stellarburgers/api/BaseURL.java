package ru.yandex.praktikum.stellarburgers.api;

import io.restassured.RestAssured;

public class BaseURL {
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

}
