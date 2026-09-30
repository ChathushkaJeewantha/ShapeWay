package lk.jiat.shapeway.models;


import com.google.firebase.firestore.Exclude;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealModel {

    @Exclude
    private String documentId;

    private String name;
    private String imageUrl;
    private int calories;
    private int protein;
    private int carbs;
    private int fats;
    private int cookingTime;
    private String dietType;   // Keto | Vegetarian | Vegan | Traditional | Paleo
    private String mealType;   // Breakfast | Lunch | Dinner | Snack
    private List<String> tags;
    private String description;
}
