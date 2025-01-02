package com.example.mealmate.Repository;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstructionsSearchResponse {

    @SerializedName("analyzedInstructions")
    private List<SpoonacularRecipeModel> ingredients;
    public List<SpoonacularRecipeModel> getIngredients(){
        return ingredients;}

}
