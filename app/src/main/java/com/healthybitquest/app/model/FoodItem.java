package com.healthybitquest.app.model;

import com.healthybitquest.app.AppConstants;

/**
 * A single food item loaded from the local foods.json asset.
 */
public class FoodItem {

    private final int id;
    private final String name;
    private final String category;
    private final String image;
    private final String explanation;

    public FoodItem(int id, String name, String category, String image, String explanation) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.category = category == null ? "" : category;
        this.image = image == null ? "" : image;
        this.explanation = explanation == null ? "" : explanation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public String getExplanation() {
        return explanation;
    }

    public boolean isHealthy() {
        return AppConstants.CATEGORY_HEALTHY.equalsIgnoreCase(category);
    }

    public boolean isValid() {
        return id > 0
                && !name.trim().isEmpty()
                && (AppConstants.CATEGORY_HEALTHY.equalsIgnoreCase(category)
                || AppConstants.CATEGORY_LESS_HEALTHY.equalsIgnoreCase(category))
                && !explanation.trim().isEmpty();
    }
}
