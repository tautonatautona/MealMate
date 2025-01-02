package com.example.mealmate.Repository;

import com.google.gson.annotations.SerializedName;

public class Meal {
    @SerializedName("title")
    public String name;
    @SerializedName("image")
    public String imageUrl;

    public Meal() {

    }

    public Meal(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}