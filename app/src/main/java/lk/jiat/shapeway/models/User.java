package lk.jiat.shapeway.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {


    private String uid;
    private String name;
    private String email;
    private String profilePicUrl;
    private String goal;
    private String somatotypes;
    private String gain;
    private String currentBody;
    private String pushUp;
    private String tpw;
    private String dailyRt;
    private String diet;
    private String wkSchedule;
    private float heightcm;
    private float heightft;
    private int weight;
    private int age;
   private String paymentNo;

}
