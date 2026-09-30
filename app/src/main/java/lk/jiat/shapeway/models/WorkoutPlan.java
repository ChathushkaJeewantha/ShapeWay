package lk.jiat.shapeway.models;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutPlan {
    private String       id;
    private String       title;
    private String       thumbnailUrl;
    private String       level;          // Newbie / Intermediate / Advanced
    private int          durationMinutes;
    private int          calories;
    private String       focusZone;      // e.g. "Legs"
    private String       healthNotice;   // advisory text
    private List<Exercise> exercises;
    private boolean      liked;


}
