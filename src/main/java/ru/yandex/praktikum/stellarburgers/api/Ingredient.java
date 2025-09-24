package ru.yandex.praktikum.stellarburgers.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Ingredient {
    private boolean success;
    private List<Data> data;
}
