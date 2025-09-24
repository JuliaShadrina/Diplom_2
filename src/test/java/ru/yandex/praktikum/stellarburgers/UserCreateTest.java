package ru.yandex.praktikum.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserCreateTest {

    private final BaseURL baseURL = new BaseURL();
    private final UserStep userStep = new UserStep();

    private User user = new User(
            UserFactory.userEmailGenerator(),
            UserFactory.userNameGenerator(),
            UserFactory.userPasswordGenerator()
    );

    private String accessToken;

    @Before
    public void setUp() {
        baseURL.setUp();
    }

    @After
    public void tearDown() {
        // Удаляем пользователя, если он был создан
        if (accessToken != null && !accessToken.isEmpty()) {
            userStep.deleteUser(accessToken);
        }
    }

    // --------- Негативные тесты ---------

    @Test
    @DisplayName("Ошибка при создании пользователя дважды")
    @Description("Негативная проверка: нельзя зарегистрировать второго идентичного пользователя")
    public void creatingAUserTwiceTest() {
        UserStep.creatingUser(user);
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");

        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без пароля")
    @Description("Негативная проверка: нельзя зарегистрировать пользователя с пустым паролем")
    public void creatingAUserWithoutAPasswordTest() {
        user.setPassword("");

        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без email")
    @Description("Негативная проверка: нельзя зарегистрировать пользователя с пустым email")
    public void creatingAUserWithoutEmailTest() {
        user.setEmail("");

        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без name")
    @Description("Негативная проверка: нельзя зарегистрировать пользователя с пустым именем")
    public void creatingAnUnnamedUserTest() {
        user.setName("");

        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // --------- Позитивные тесты ---------

    @Test
    @DisplayName("Успешное создание пользователя")
    @Description("Позитивная проверка: можно зарегистрировать нового пользователя")
    public void creatingUserTest() {
        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        // сохраняем токен для корректного удаления в tearDown
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");
    }
}