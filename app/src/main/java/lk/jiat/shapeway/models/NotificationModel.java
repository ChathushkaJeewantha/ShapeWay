package lk.jiat.shapeway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationModel {
    private String id;
    private String title;
    private String message;
    private String topic;
    private Object timestamp;
}
