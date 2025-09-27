package ru.yandex.praktikum.stellarburgers.ordertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.stellarburgers.api.base.BaseURL;
import ru.yandex.praktikum.stellarburgers.api.models.Ingredient;
import ru.yandex.praktikum.stellarburgers.api.models.Order;
import ru.yandex.praktikum.stellarburgers.api.models.User;
import ru.yandex.praktikum.stellarburgers.api.steps.IngredientStep;
import ru.yandex.praktikum.stellarburgers.api.steps.OrderStep;
import ru.yandex.praktikum.stellarburgers.api.steps.UserStep;
import ru.yandex.praktikum.stellarburgers.api.utils.UserFactory;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderGetTest {

    private final BaseURL baseURL = new BaseURL();
    private final UserStep userStep = new UserStep();
    private final OrderStep orderStep = new OrderStep();
    private final IngredientStep ingredientStep = new IngredientStep();

    private final User user = new User(
            UserFactory.userEmailGenerator(),
            UserFactory.userNameGenerator(),
            UserFactory.userPasswordGenerator()
    );

    private String accessToken;
    private List<String> ingredients;
    private Order order;

    @Before
    public void setUp() {
        baseURL.setUp();

        // Получаем список ингредиентов
        Ingredient ingredientList = ingredientStep.getIngredient();
        ingredients = new ArrayList<>();
        ingredients.add(ingredientList.getData().get(1).getId());
        ingredients.add(ingredientList.getData().get(2).getId());
        ingredients.add(ingredientList.getData().get(3).getId());

        // Создаём пользователя и логинимся
        UserStep.creatingUser(user);
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");

        // Формируем заказ
        order = new Order(ingredients);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userStep.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Ошибка при получении заказов без авторизации")
    @Description("Негативная проверка: неавторизованный пользователь не может получить список заказов")
    public void getOrderNotAuthUserTest() {
        orderStep.getOrdersNotAuthUser()
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Позитивная проверка: авторизованный пользователь может получить список заказов")
    public void getOrderAuthUserTest() {
        orderStep.getOrdersAuthUser(accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }
}