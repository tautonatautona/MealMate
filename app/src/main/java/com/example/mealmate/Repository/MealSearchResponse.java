package com.example.mealmate.Repository;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MealSearchResponse {

    @SerializedName("results")
    private List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }
}
