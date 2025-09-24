package ru.yandex.praktikum.stellarburgers;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {

    private static final String INCORRECT_EMAIL = UserFactory.userEmailGenerator();
    private static final String INCORRECT_PASSWORD = UserFactory.userPasswordGenerator();

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

        // Создаём тестового пользователя
        UserStep.creatingUser(user);

        // Сохраняем accessToken для дальнейшего удаления
        accessToken = userStep.loginUser(user)
                .then()
                .extract()
                .path("accessToken");
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
    @DisplayName("Ошибка при авторизации с некорректным email")
    @Description("Негативная проверка: пользователь не может авторизоваться с неверным email")
    public void authorizationWithIncorrectEmailTest() {
        user.setEmail(INCORRECT_EMAIL);

        userStep.loginUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Ошибка при авторизации с некорректным паролем")
    @Description("Негативная проверка: пользователь не может авторизоваться с неверным паролем")
    public void authorizationWithIncorrectPasswordTest() {
        user.setPassword(INCORRECT_PASSWORD);

        userStep.loginUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    // --------- Позитивные тесты ---------

    @Test
    @DisplayName("Успешная авторизация пользователя")
    @Description("Позитивная проверка: пользователь может авторизоваться с корректными данными")
    public void authorizationUserTest() {
        userStep.loginUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }
}