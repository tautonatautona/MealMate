package com.example.mealmate.Model;

import com.example.mealmate.Repository.SpoonacularRecipeModel;

import java.util.List;

public class Recipe extends SpoonacularRecipeModel {

    // Default constructor for Firebase
    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    // Parameterized constructor
    public Recipe(String title, String imageURL, List<String> ingredients, String instructions, String description) {
        super(title, imageURL, ingredients, instructions, description); // Pass values to the parent constructor
    }

    // Override toString method if necessary
    @Override
    public String toString() {
        return "Recipe{" +
                "title='" + getTitle() + '\'' +
                ", imageURL='" + getImageURL() + '\'' +
                ", ingredients=" + getIngredients() +
                ", instructions='" + getInstructions() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}
