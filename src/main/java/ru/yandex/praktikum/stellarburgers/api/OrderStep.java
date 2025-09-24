package ru.yandex.praktikum.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderStep {

    private static final String ORDER_PATH = "api/orders";

    @Step("Создание заказа авторизованным пользователем")
    public Response createOrderAuthUser(Order order, String accessToken) {
        return given().log().all()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Создание заказа неавторизованным пользователем")
    public Response createOrderNotAuthUser(Order order) {
        return given().log().all()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получение заказов авторизованного пользователя")
    public Response getOrdersAuthUser(String accessToken) {
        return given().log().all()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .get(ORDER_PATH);
    }

    @Step("Получение заказов неавторизованного пользователя")
    public Response getOrdersNotAuthUser() {
        return given()
                .header("Content-type", "application/json")
                .get(ORDER_PATH);
    }
}
