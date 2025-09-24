package ru.yandex.praktikum.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserStep {

    private static final String USER_CREATE_PATH = "api/auth/register";
    private static final String USER_LOGIN_PATH = "api/auth/login";
    private static final String USER_PATCH_PATH = "api/auth/user";
    private static final String USER_DELETE_PATH = "api/auth/user";

    @Step("Создания пользователя")
    public static Response creatingUser(User user) {
        return given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(USER_CREATE_PATH);

    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(USER_LOGIN_PATH);

    }

    @Step("Изменение данных пользователя")
    public Response patchUser(User user, String accessToken) {
        return given().log().all()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .body(user)
                .patch(USER_PATCH_PATH);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .when()
                .delete(USER_DELETE_PATH);
    }
}