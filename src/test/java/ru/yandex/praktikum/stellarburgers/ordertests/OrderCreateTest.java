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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest {

    private static final String INGREDIENT_WITH_WRONG_HASH = "ingredientwithwronghash";

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

        // Получаем список ингредиентов с API
        Ingredient ingredientList = ingredientStep.getIngredient();
        ingredients = new ArrayList<>();
        ingredients.add(ingredientList.getData().get(1).getId());
        ingredients.add(ingredientList.getData().get(2).getId());
        ingredients.add(ingredientList.getData().get(3).getId());

        // Создаём пользователя и логинимся
        UserStep.creatingUser(user);
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");

        // Собираем заказ
        order = new Order(ingredients);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userStep.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Позитивная проверка: заказ можно создать авторизованным пользователем")
    public void createOrderAuthUserTest() {
        orderStep.createOrderAuthUser(order, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    @Description("Позитивная проверка: заказ можно создать без авторизации")
    public void createOrderNotAuthUserTest() {
        orderStep.createOrderNotAuthUser(order)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при создании заказа без ингредиентов (авторизованный пользователь)")
    @Description("Негативная проверка: заказ без ингредиентов должен вернуть 400 Bad Request")
    public void createOrderNoIngredientsTest() {
        ingredients.clear();
        order = new Order(ingredients);

        orderStep.createOrderAuthUser(order, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка при создании заказа с неверным id ингредиента")
    @Description("Негативная проверка: заказ с неверным хешем ингредиента должен вернуть 500 Internal Server Error")
    public void createOrderWithWrongHashTest() {
        ingredients.clear();
        ingredients.add(INGREDIENT_WITH_WRONG_HASH);
        order = new Order(ingredients);

        orderStep.createOrderAuthUser(order, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}