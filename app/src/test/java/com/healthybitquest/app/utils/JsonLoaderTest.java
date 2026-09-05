package com.healthybitquest.app.utils;

import com.healthybitquest.app.AppConstants;
import com.healthybitquest.app.model.FoodItem;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonLoaderTest {

    private static final String SAMPLE_JSON =
            "{"
                    + "\"foods\":["
                    + "{\"id\":1,\"name\":\"Apple\",\"category\":\"healthy\",\"image\":\"food_apple\","
                    + "\"explanation\":\"Apples are a nutritious fruit.\"},"
                    + "{\"id\":2,\"name\":\"Candy\",\"category\":\"less_healthy\",\"image\":\"food_candy\","
                    + "\"explanation\":\"Candy has lots of sugar.\"}"
                    + "]"
                    + "}";

    @Test
    public void parseFoodsLoadsValidItems() throws JSONException {
        JsonLoader loader = new JsonLoader();
        List<FoodItem> foods = loader.parseFoods(SAMPLE_JSON);

        assertEquals(2, foods.size());
        assertEquals("Apple", foods.get(0).getName());
        assertTrue(foods.get(0).isHealthy());
        assertEquals(AppConstants.CATEGORY_LESS_HEALTHY, foods.get(1).getCategory());
    }

    @Test
    public void bundledFoodsJsonLoadsAtLeastTenItems() throws Exception {
        JsonLoader loader = new JsonLoader();
        List<FoodItem> foods = loader.parseFoods(readBundledFoodsJson());
        assertTrue(foods.size() >= 10);
        boolean hasHealthy = false;
        boolean hasLessHealthy = false;
        for (FoodItem food : foods) {
            if (food.isHealthy()) {
                hasHealthy = true;
            } else {
                hasLessHealthy = true;
            }
        }
        assertTrue(hasHealthy);
        assertTrue(hasLessHealthy);
    }

    private String readBundledFoodsJson() throws IOException {
        Path[] candidates = new Path[] {
                Paths.get("src", "main", "assets", "foods.json"),
                Paths.get("app", "src", "main", "assets", "foods.json")
        };
        for (Path path : candidates) {
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            }
        }
        throw new IOException("Could not find bundled foods.json");
    }

    @Test(expected = JSONException.class)
    public void emptyJsonThrows() throws JSONException {
        new JsonLoader().parseFoods(" ");
    }

    @Test(expected = JSONException.class)
    public void missingFoodsArrayThrows() throws JSONException {
        new JsonLoader().parseFoods("{\"items\":[]}");
    }
}
