package com.healthybitquest.app.utils;

import com.healthybitquest.app.model.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import android.content.Context;

/**
 * Loads food questions from the bundled assets/foods.json file.
 * Loading runs off the main thread using CompletableFuture.
 */
public class JsonLoader {

    public static final String FOODS_ASSET = "foods.json";

    public List<FoodItem> parseFoods(String json) throws JSONException {
        if (json == null || json.trim().isEmpty()) {
            throw new JSONException("Food data is empty.");
        }

        JSONObject root = new JSONObject(json);
        JSONArray array = root.optJSONArray("foods");
        if (array == null) {
            throw new JSONException("Missing foods array.");
        }

        List<FoodItem> foods = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            FoodItem food = new FoodItem(
                    item.optInt("id", 0),
                    item.optString("name", ""),
                    item.optString("category", ""),
                    item.optString("image", ""),
                    item.optString("explanation", "")
            );
            if (food.isValid()) {
                foods.add(food);
            }
        }

        if (foods.isEmpty()) {
            throw new JSONException("No valid food items found.");
        }
        return foods;
    }

    public List<FoodItem> loadFromAssets(Context context) throws IOException, JSONException {
        String json = readAsset(context, FOODS_ASSET);
        return parseFoods(json);
    }

    public CompletableFuture<List<FoodItem>> loadFromAssetsAsync(Context context) {
        Context appContext = context.getApplicationContext();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadFromAssets(appContext);
            } catch (Exception exception) {
                throw new CompletionException(exception);
            }
        });
    }

    private String readAsset(Context context, String fileName) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (InputStream inputStream = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }
}
