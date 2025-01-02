package com.example.mealmate.Repository;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IngredientSearchResponse {
    @SerializedName("description")
    private String description;

    @SerializedName("extendedIngredients")
    private List<String> ingredients;

    @SerializedName("instructions")
    private String instructions;

    // No-argument constructor
    public IngredientSearchResponse() {
        this.description = "";
        this.ingredients = null;
        this.instructions = "";
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Getter for ingredients
    public List<String> getIngredients() {
        return ingredients;
    }

    // Getter for instructions
    public String getInstructions() {
        return instructions;
    }

    // Setters (if needed for Firebase or other purposes)
    public void setDescription(String description) {
        this.description = description;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}