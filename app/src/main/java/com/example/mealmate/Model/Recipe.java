package com.example.mealmate.Model;

public class Recipe {
    private String title;
    private String imageUrl;

    public Recipe(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

