package ru.yandex.praktikum.stellarburgers.usertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.base.BaseURL;
import ru.yandex.praktikum.stellarburgers.api.models.User;
import ru.yandex.praktikum.stellarburgers.api.steps.UserStep;
import ru.yandex.praktikum.stellarburgers.api.utils.UserFactory;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserChangeTest {

    private static final String UPDATE_EMAIL = UserFactory.userEmailGenerator();
    private static final String UPDATE_NAME = UserFactory.userNameGenerator();

    private final BaseURL baseURL = new BaseURL();
    private final UserStep userStep = new UserStep();

    private final User user = new User(
            UserFactory.userEmailGenerator(),
            UserFactory.userNameGenerator(),
            UserFactory.userPasswordGenerator()
    );

    private String accessToken;

    @Before
    public void setUp() {
        baseURL.setUp();
        // создаём и логиним пользователя
        UserStep.creatingUser(user);
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userStep.deleteUser(accessToken);
        }
    }

    // --------- Негативные тесты ---------

    @Test
    @DisplayName("Ошибка при смене email без авторизации")
    @Description("Негативная проверка: неавторизованный пользователь не может изменить email")
    public void changeNotAuthUserEmailTest() {
        accessToken = "";
        user.setEmail(UPDATE_EMAIL);

        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Ошибка при смене name без авторизации")
    @Description("Негативная проверка: неавторизованный пользователь не может изменить name")
    public void changeNotAuthUserNameTest() {
        accessToken = "";
        user.setName(UPDATE_NAME);

        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Ошибка при смене email на уже существующий")
    @Description("Негативная проверка: нельзя изменить email на уже используемый другим пользователем")
    public void changeAuthUserWithExistEmailTest() {
        // email, который уже используется первым пользователем
        String usedEmail = user.getEmail();

        // создаём второго пользователя
        User anotherUser = new User(
                UserFactory.userEmailGenerator(),
                UserFactory.userNameGenerator(),
                UserFactory.userPasswordGenerator()
        );
        String newAccessToken = UserStep.creatingUser(anotherUser)
                .jsonPath().getString("accessToken");

        // пытаемся второму пользователю установить email первого
        User patchAnotherUser = new User(usedEmail, anotherUser.getName(), anotherUser.getPassword());

        userStep.patchUser(patchAnotherUser, newAccessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

        // удаляем второго пользователя
        userStep.deleteUser(newAccessToken);
    }

    // --------- Позитивные тесты ---------

    @Test
    @DisplayName("Смена email авторизованным пользователем")
    @Description("Позитивная проверка: авторизованный пользователь может изменить email")
    public void changeAuthUserEmailTest() {
        user.setEmail(UPDATE_EMAIL);

        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Смена name авторизованным пользователем")
    @Description("Позитивная проверка: авторизованный пользователь может изменить name")
    public void changeAuthUserNameTest() {
        user.setName(UPDATE_NAME);

        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getName()));
    }
}