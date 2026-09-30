package lk.jiat.shapeway.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.jiat.shapeway.R;

public class ActivityQuestion1 extends AppCompatActivity {

    ProgressBar progressBar;
    TextView txtProgress;
    int totalQuestions = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.questionProgress);
        txtProgress = findViewById(R.id.txtProgress);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.qContainer, new Q1())
                    .commit();
        }




    }
    public void updateProgress(int step){
        int currentProgress = progressBar.getProgress();

        ObjectAnimator animation = ObjectAnimator.ofInt(
                progressBar,
                "progress",
                currentProgress,
                step
        );

        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        if (totalQuestions==10){

            txtProgress.setText("Question Session Completed");

        }else{

        txtProgress.setText("Question " + step + " of " + totalQuestions);

        }

    }

}