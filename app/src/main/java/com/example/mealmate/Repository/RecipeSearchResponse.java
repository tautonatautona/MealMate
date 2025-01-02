package com.example.mealmate.Repository;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeSearchResponse {
    @SerializedName("recipes")  // This should match the actual JSON key from Spoonacular API
    private List<SpoonacularRecipeModel> results;

    @SerializedName("totalResults")
    private int totalResults;

    // Getter method to match the method call in HomePageFragment
    public List<SpoonacularRecipeModel> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    // Optional setter methods if needed
    public void setResults(List<SpoonacularRecipeModel> results) {
        this.results = results;
    }
}