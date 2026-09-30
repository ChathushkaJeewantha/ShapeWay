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
public class Workout {

    private String title;
    private String youtubeVideoId;
    private String focusZone;
    private String level;

    private long durationMinutes;
    private long calories;

    private boolean liked;

}
