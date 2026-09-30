package lk.jiat.shapeway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    private String id;
    private String title;
    private String thumbnailUrl;
    private String youtubeVideoId;   // e.g. "dQw4w9WgXcQ"
    private int    durationSeconds;  // used by the timer
    private int    sets;
    private String category;         // e.g. "Legs", "Core"


}
