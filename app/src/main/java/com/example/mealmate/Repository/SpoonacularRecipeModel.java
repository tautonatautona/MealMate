package com.example.mealmate.Repository;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpoonacularRecipeModel {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String imageURL;

    @SerializedName("ingredients")
    private List<String> ingredients;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("description")
    private String description;

    @Exclude // Exclude from Firebase serialization
    private String key;

    public SpoonacularRecipeModel() {
    }

    public SpoonacularRecipeModel(String title, String imageURL, List<String> ingredients, String instructions, String description) {
        setTitle(title);
        this.imageURL = imageURL;
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        this.instructions = instructions;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getInstructionsAsList() {
        if (instructions == null || instructions.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(instructions.split("\\.\\s*"));
    }
}
