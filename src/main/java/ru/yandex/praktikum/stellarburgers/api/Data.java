package ru.yandex.praktikum.stellarburgers.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Data {

    @JsonProperty("_id")   // 🔹 Jackson теперь поймет поле _id
    private String id;

    private String name;
    private String type;
    private int proteins;
    private int fat;
    private int carbohydrates;
    private int calories;
    private float price;
    private String image;

    @JsonProperty("image_mobile")
    private String imageMobile;

    @JsonProperty("image_large")
    private String imageLarge;

    @JsonProperty("__v")
    private int v;
}