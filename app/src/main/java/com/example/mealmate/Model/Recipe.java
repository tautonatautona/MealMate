package com.example.mealmate.Model;

import java.util.List;

public class Recipe {
    private List<InstructionIngredient> ingredients;
    private String dataTitle;
    private String dataImage;
    private String dataDesc;
    private String id;
    private String key;
    private List<RecipeInstruction> instructions;
    private String analyzedInstructions;

    // No-argument constructor (required by Firebase)
    public Recipe() {
        // Initialize with default values if necessary
        this.ingredients = null;
        this.dataTitle = "";
        this.dataImage = "";
        this.dataDesc = "";
        this.key = "";
        this.id = "";
        this.analyzedInstructions = "";
        this.instructions = null;
    }

    // Updated constructor (your existing one)
    public Recipe(String id , String dataTitle, String dataImage, String dataDesc, String key,
                  List<InstructionIngredient> ingredients, String analyzedInstructions, List<RecipeInstruction> instructions) {
        this.dataTitle = dataTitle;
        this.dataImage = dataImage;
        this.dataDesc = dataDesc;
        this.key = key;
        this.id = id;
        this.ingredients = ingredients;
        this.analyzedInstructions = analyzedInstructions;
        this.instructions = instructions;
    }

    // Getters
    public String getDataTitle() {
        return dataTitle;
    }

    public String getId(){
        return id;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getKey() {
        return key;
    }

    public List<InstructionIngredient> getIngredients() {
        return ingredients;
    }

    public String getAnalyzedInstructions() {
        return analyzedInstructions;
    }

    public List<RecipeInstruction> getInstructions() {
        return instructions;
    }

    // Setter for key (optional)
    public void setKey(String key) {
        this.key = key;
    }

    // Setter for dataTitle
    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    // Setter for dataImage
    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    // Setter for dataDesc
    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    // Setter for ingredients
    public void setIngredients(List<InstructionIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    // Setter for analyzedInstructions
    public void setAnalyzedInstructions(String analyzedInstructions) {
        this.analyzedInstructions = analyzedInstructions;
    }


    // Inner classes with no-argument constructors
    public static class RecipeInstruction {
        private int number;
        private String step;
        private static List<InstructionIngredient> ingredients;
        private List<Equipment> equipment;
        private InstructionLength length;

        // No-argument constructor
        public RecipeInstruction() {
            // Initialize with default values
            this.number = 0;
            this.step = "";
            this.ingredients = null;
            this.equipment = null;
            this.length = null;
        }

        // Existing constructor with parameters
        public RecipeInstruction(int number, String step,
                                 List<InstructionIngredient> ingredients,
                                 List<Equipment> equipment,
                                 InstructionLength length) {
            this.number = number;
            this.step = step;
            this.ingredients = ingredients;
            this.equipment = equipment;
            this.length = length;
        }

        // Getters
        public int getNumber() {
            return number;
        }

        public String getStep() {
            return step;
        }

        public static List<InstructionIngredient> getIngredients() {
            return ingredients;
        }

        public List<Equipment> getEquipment() {
            return equipment;
        }

        public InstructionLength getLength() {
            return length;
        }
    }

    public static class InstructionIngredient {
        private int id;
        private String name;
        private String localizedName;
        private String image;

        // No-argument constructor
        public InstructionIngredient() {
            // Initialize with default values
            this.id = 0;
            this.name = "";
            this.localizedName = "";
            this.image = "";
        }

        // Existing constructor with parameters
        public InstructionIngredient(int id, String name, String localizedName, String image) {
            this.id = id;
            this.name = name;
            this.localizedName = localizedName;
            this.image = image;
        }

        // Getters
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLocalizedName() {
            return localizedName;
        }

        public String getImage() {
            return image;
        }
    }

    public static class Equipment {
        private int id;
        private String name;
        private String localizedName;
        private String image;
        private EquipmentTemperature temperature;

        // No-argument constructor
        public Equipment() {
            // Initialize with default values
            this.id = 0;
            this.name = "";
            this.localizedName = "";
            this.image = "";
            this.temperature = null;
        }

        // Existing constructor with parameters
        public Equipment(int id, String name, String localizedName, String image, EquipmentTemperature temperature) {
            this.id = id;
            this.name = name;
            this.localizedName = localizedName;
            this.image = image;
            this.temperature = temperature;
        }

        // Getters
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLocalizedName() {
            return localizedName;
        }

        public String getImage() {
            return image;
        }

        public EquipmentTemperature getTemperature() {
            return temperature;
        }
    }

    public static class EquipmentTemperature {
        private double number;
        private String unit;

        // No-argument constructor
        public EquipmentTemperature() {
            // Initialize with default values
            this.number = 0;
            this.unit = "";
        }

        // Existing constructor with parameters
        public EquipmentTemperature(double number, String unit) {
            this.number = number;
            this.unit = unit;
        }

        // Getters
        public double getNumber() {
            return number;
        }

        public String getUnit() {
            return unit;
        }
    }

    public static class InstructionLength {
        private int number;
        private String unit;

        // No-argument constructor
        public InstructionLength() {
            // Initialize with default values
            this.number = 0;
            this.unit = "";
        }

        // Existing constructor with parameters
        public InstructionLength(int number, String unit) {
            this.number = number;
            this.unit = unit;
        }

        // Getters
        public int getNumber() {
            return number;
        }

        public String getUnit() {
            return unit;
        }
    }
}
