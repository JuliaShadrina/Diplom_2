package ru.yandex.praktikum.stellarburgers.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Order {
    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public Order() {
    }
}
