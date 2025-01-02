package com.example.mealmate.Repository;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonAcularAPI {

    String BASE_URL = "https://api.spoonacular.com/";

    // Search for recipes with filters
    @GET("recipes/complexSearch")
    Call<RecipeSearchResponse> searchRecipes(
            @Query("query") String query, // Query can be Breakfast, Lunch, or Dinner
            @Query("diet") String diet,
            @Query("intolerances") String intolerances,
            @Query("cuisine") String cuisine,
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    // Fetch meal recipes (simplified, can be used with defaults)
    @GET("recipes/complexSearch")
    Call<MealSearchResponse> searchMealRecipes(
            @Query("query") String query,
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    // Get detailed recipe information
    @GET("recipes/{id}/information")
    Call<IngredientSearchResponse> getRecipeInformation(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey,
            @Query("includeNutrition") boolean includeNutrition
    );

    // Get recipe instructions
    @GET("recipes/{id}/analyzedInstructions")
    Call<InstructionsSearchResponse> getRecipeInstructions(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey,
            @Query("stepBreakdown") boolean stepBreakdown
    );

    // Fetch random recipes
    @GET("recipes/random")
    Call<RecipeSearchResponse> fetchRandomRecipes(
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    // Search for wine pairings (generalized query)
    @GET("food/wine/pairing")
    Call<RecipeSearchResponse> searchWines(
            @Query("food") String food,
            @Query("apiKey") String apiKey
    );

    // Optional helper method for default recipe search
    default Call<RecipeSearchResponse> searchRecipes(String query, String apiKey) {
        return searchRecipes(query, null, null, null, apiKey, 10);
    }
}
