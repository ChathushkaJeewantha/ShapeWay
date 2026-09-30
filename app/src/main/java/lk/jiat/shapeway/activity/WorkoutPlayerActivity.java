package lk.jiat.shapeway.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;
import java.util.Locale;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.Exercise;
import lk.jiat.shapeway.models.WorkoutPlan;

public class WorkoutPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_WORKOUT_ID = "workout_id";

    // Views
    private ImageView ivThumbnail, ivPlayBtn, ivClose, ivNextThumb;
    private FrameLayout flPlayOverlay;
    private YouTubePlayerView youtubePlayerView;
    private TextView tvTimer, tvSetsValue, tvRepCount, tvNextExerciseName, tvNextDuration;
    private ProgressBar pbExercise;
    private MaterialButton btnPrev, btnNext;

    // State
    private YouTubePlayer youTubePlayer;
    private WorkoutPlan plan;
    private List<Exercise> exercises;
    private int                currentIndex = 0;
    private CountDownTimer countDownTimer;
    private boolean            isPlaying = false;
    private int                setsCompleted = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_player);


        bindViews();
        setupYouTubePlayer();
        setupButtons();

        String workoutId = getIntent().getStringExtra(EXTRA_WORKOUT_ID);
        if (workoutId != null) loadWorkout(workoutId);

    }

    // ── Bind Views ────────────────────────────────────────────────────────
    private void bindViews() {
        ivThumbnail         = findViewById(R.id.ivThumbnail);
        ivPlayBtn           = findViewById(R.id.ivPlayBtn);
        flPlayOverlay       = findViewById(R.id.flPlayOverlay);
        youtubePlayerView   = findViewById(R.id.youtubePlayerView);
        ivClose             = findViewById(R.id.ivClose);
        ivNextThumb         = findViewById(R.id.ivNextThumb);
        tvTimer             = findViewById(R.id.tvTimer);
        tvSetsValue         = findViewById(R.id.tvSetsValue);
        tvRepCount          = findViewById(R.id.tvRepCount);
        tvNextExerciseName  = findViewById(R.id.tvNextExerciseName);
        tvNextDuration      = findViewById(R.id.tvNextDuration);
        pbExercise          = findViewById(R.id.pbExercise);
        btnPrev             = findViewById(R.id.btnPrev);
        btnNext             = findViewById(R.id.btnNext);
    }

    // ── YouTube Player init ───────────────────────────────────────────────
    private void setupYouTubePlayer() {
        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer player) {
                youTubePlayer = player;
                // Cue first video silently (no autoplay)
                if (exercises != null && !exercises.isEmpty()) {
                    cueVideo(exercises.get(currentIndex).getYoutubeVideoId());
                }
            }
        });

        // Play button overlay tap → show player, start video + timer
        flPlayOverlay.setOnClickListener(v -> startCurrentExercise());
        ivPlayBtn.setOnClickListener(v -> startCurrentExercise());
    }

    // ── Load Firestore ────────────────────────────────────────────────────
    private void loadWorkout(String id) {
        FirebaseFirestore.getInstance()
                .collection("workouts").document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    plan      = doc.toObject(WorkoutPlan.class);
                    if (plan == null) return;
                    exercises = plan.getExercises();
                    currentIndex = 0;
                    renderCurrentExercise();
                });
    }

    // ── Render current exercise ───────────────────────────────────────────
    private void renderCurrentExercise() {
        if (exercises == null || exercises.isEmpty()) return;
        stopTimer();

        Exercise current = exercises.get(currentIndex);

        // Thumbnail
        if (current.getThumbnailUrl() != null && !current.getThumbnailUrl().isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(current.getThumbnailUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_workout_placeholder)
                    .error(R.drawable.ic_workout_placeholder)
                    .into(ivThumbnail);
        } else {
            ivThumbnail.setImageResource(R.drawable.ic_workout_placeholder);
        }

        // Reset play overlay
        youtubePlayerView.setVisibility(View.GONE);
        flPlayOverlay.setVisibility(View.VISIBLE);
        isPlaying = false;

        // Timer reset
        int sec = current.getDurationSeconds();
        tvTimer.setText(formatTime(sec));
        pbExercise.setMax(sec);
        pbExercise.setProgress(sec);

        // Sets
        tvSetsValue.setText(String.valueOf(setsCompleted));
        tvRepCount.setText(String.valueOf(current.getSets()));

        // Cue video (without auto-play)
        if (youTubePlayer != null) cueVideo(current.getYoutubeVideoId());

        // Next exercise preview
        renderNextPreview();
    }


    private void renderNextPreview() {
        int nextIdx = currentIndex + 1;
        if (exercises == null || nextIdx >= exercises.size()) {
            tvNextExerciseName.setText("Workout Complete!");
            tvNextDuration.setText("");
            ivNextThumb.setImageResource(R.drawable.ic_workout_placeholder);
            return;
        }
        Exercise next = exercises.get(nextIdx);
        tvNextExerciseName.setText(next.getTitle());
        tvNextDuration.setText(formatTime(next.getDurationSeconds()));
        if (next.getThumbnailUrl() != null && !next.getThumbnailUrl().isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(next.getThumbnailUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_workout_placeholder)
                    .error(R.drawable.ic_workout_placeholder)
                    .into(ivNextThumb);
        } else {
            ivNextThumb.setImageResource(R.drawable.ic_workout_placeholder);
        }
    }

    // ── Start current exercise (play video + timer) ───────────────────────
    private void startCurrentExercise() {
        if (exercises == null || exercises.isEmpty()) return;
        Exercise current = exercises.get(currentIndex);

        // Show YouTube player, hide thumbnail overlay
        youtubePlayerView.setVisibility(View.VISIBLE);
        flPlayOverlay.setVisibility(View.GONE);
        isPlaying = true;

        // Play video
        if (youTubePlayer != null) {
            youTubePlayer.loadVideo(current.getYoutubeVideoId(), 0);
        }

        // Start countdown timer
        startTimer(current.getDurationSeconds());
    }

    // ── Timer ─────────────────────────────────────────────────────────────
    private void startTimer(int seconds) {
        stopTimer();
        pbExercise.setMax(seconds);
        pbExercise.setProgress(seconds);

        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisLeft) {
                int secLeft = (int) (millisLeft / 1000);
                tvTimer.setText(formatTime(secLeft));
                pbExercise.setProgress(secLeft);
            }
            @Override
            public void onFinish() {
                tvTimer.setText("0:00");
                pbExercise.setProgress(0);
                // Auto advance to next
                setsCompleted++;
                tvSetsValue.setText(String.valueOf(setsCompleted));
                goNext();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────
    private void setupButtons() {
        btnNext.setOnClickListener(v -> goNext());
        btnPrev.setOnClickListener(v -> goPrev());
        ivClose.setOnClickListener(v -> finish());
    }

    private void goNext() {
        if (exercises == null) return;
        if (currentIndex < exercises.size() - 1) {
            currentIndex++;
            renderCurrentExercise();
        } else {
            // Last exercise done — show completion
            showCompletion();
        }
    }

    private void goPrev() {
        if (currentIndex > 0) {
            currentIndex--;
            renderCurrentExercise();
        }
    }

    // ── Completion ────────────────────────────────────────────────────────
    private void showCompletion() {
        stopTimer();
        if (youTubePlayer != null) youTubePlayer.pause();
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("🎉 Workout Complete!")
                .setMessage("Great job! You finished all exercises.")
                .setPositiveButton("Done", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private void cueVideo(String videoId) {
        if (youTubePlayer != null && videoId != null) {
            youTubePlayer.cueVideo(videoId, 0);
        }
    }

    private String formatTime(int totalSeconds) {
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        youtubePlayerView.release();
    }

}