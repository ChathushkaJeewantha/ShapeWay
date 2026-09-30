package lk.jiat.shapeway.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder

@NoArgsConstructor
public class MealSection {
    private String sectionTitle; // Breakfast, Lunch, Dinner, Snack
    private List<MealModel> meals;

    public MealSection(String sectionTitle, List<MealModel> meals) {
        this.sectionTitle = sectionTitle;
        this.meals = meals;
    }
}
