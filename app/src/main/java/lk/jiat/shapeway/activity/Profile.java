package lk.jiat.shapeway.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.User;

public class Profile extends Fragment {


    private ShapeableImageView ivProfilePic;
    private ImageView ivEditPhoto, ivEditProfile, ivBmiIcon;
    private TextView tvName, tvEmail, tvAge, tvAgeStat;
    private TextView tvHeight, tvWeight;
    private TextView tvBmiValue, tvBmiMessage;
    private View viewBmiProgress, viewBmiThumb;
    private LinearLayout llBmiStatus;
    private MaterialButton btnSignOut;

    // Profile detail rows
    private View rowGoal, rowSomatotype, rowCurrentBody, rowGain;
    private View rowPushUp, rowTpw, rowDailyRt, rowWkSchedule, rowDiet;


    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private String currentUid;


    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) uploadProfilePic(imageUri);
                        }
                    }
            );

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView callBtn = view.findViewById(R.id.callBtn);
        callBtn.setOnClickListener(view1 -> {


            String phoneNumber = "tel:0771234567"; // your number here
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(phoneNumber));
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE}, 1);

            } else {
                startActivity(intent);
            }


        });

        // Init Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        if (auth.getCurrentUser() == null) return;
        currentUid = auth.getCurrentUser().getUid();

        bindViews(view);
        setupListeners();
        loadUserData();

    }


    // ── Bind views ────────────────────────────────────────────────────────
    private void bindViews(View v) {
        ivProfilePic = v.findViewById(R.id.ivProfilePic);
        ivEditPhoto = v.findViewById(R.id.ivEditPhoto);
        ivEditProfile = v.findViewById(R.id.ivEditProfile);
        ivBmiIcon = v.findViewById(R.id.ivBmiIcon);
        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvAge = v.findViewById(R.id.tvAge);
        tvAgeStat = v.findViewById(R.id.tvAgeStat);
        tvHeight = v.findViewById(R.id.tvHeight);
        tvWeight = v.findViewById(R.id.tvWeight);
        tvBmiValue = v.findViewById(R.id.tvBmiValue);
        tvBmiMessage = v.findViewById(R.id.tvBmiMessage);
        viewBmiProgress = v.findViewById(R.id.viewBmiProgress);
        viewBmiThumb = v.findViewById(R.id.viewBmiThumb);
        llBmiStatus = v.findViewById(R.id.llBmiStatus);
        btnSignOut = v.findViewById(R.id.btnSignOut);

        rowGoal = v.findViewById(R.id.rowGoal);
        rowSomatotype = v.findViewById(R.id.rowSomatotype);
        rowCurrentBody = v.findViewById(R.id.rowCurrentBody);
        rowGain = v.findViewById(R.id.rowGain);
        rowPushUp = v.findViewById(R.id.rowPushUp);
        rowTpw = v.findViewById(R.id.rowTpw);
        rowDailyRt = v.findViewById(R.id.rowDailyRt);
        rowWkSchedule = v.findViewById(R.id.rowWkSchedule);
        rowDiet = v.findViewById(R.id.rowDiet);
    }

    // ── Click listeners ───────────────────────────────────────────────────
    private void setupListeners() {
        ivEditPhoto.setOnClickListener(v -> openGallery());
        ivEditProfile.setOnClickListener(v -> openEditProfileActivity());
        btnSignOut.setOnClickListener(v -> signOut());
    }


    // ── Load Firestore data ───────────────────────────────────────────────
    private void loadUserData() {
        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(this::populateUI)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show()
                );
    }


    private void populateUI(DocumentSnapshot doc) {
        if (!doc.exists() || getContext() == null) return;

        User user = doc.toObject(User.class);
        if (user == null) return;

        // ── Header info ──
        tvName.setText(user.getName() != null ? user.getName() : "—");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "—");
        String ageStr = user.getAge() > 0 ? user.getAge() + " yrs" : "—";
        tvAge.setText(ageStr);
        tvAgeStat.setText(user.getAge() > 0 ? String.valueOf(user.getAge()) : "—");

        // ── Body stats ──
        if (user.getHeightcm() > 0) {
            tvHeight.setText(String.format("%.0f cm", user.getHeightcm()));
        }
        if (user.getWeight() > 0) {
            tvWeight.setText(user.getWeight() + " kg");
        }

        // ── Profile image ──
        if (user.getProfilePicUrl() != null && !user.getProfilePicUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePicUrl())
                    .apply(new RequestOptions().circleCrop().placeholder(R.drawable.ic_default_avatar))
                    .into(ivProfilePic);
        }

        // ── BMI ──
        if (user.getHeightcm() > 0 && user.getWeight() > 0) {
            double heightM = user.getHeightcm() / 100.0;
            double bmi = user.getWeight() / (heightM * heightM);
            updateBmiUI(bmi);
        }

        // ── Detail rows ──
        setRow(rowGoal, "Goal", user.getGoal());
        setRow(rowSomatotype, "Body Type", user.getSomatotypes());
        setRow(rowCurrentBody, "Current Body", user.getCurrentBody());
        setRow(rowGain, "Target", user.getGain());
        setRow(rowPushUp, "Push-Ups", user.getPushUp());
        setRow(rowTpw, "Training / Week", user.getTpw());
        setRow(rowDailyRt, "Daily Routine", user.getDailyRt());
        setRow(rowWkSchedule, "Workout Schedule", user.getWkSchedule());
        setRow(rowDiet, "Diet Plan", user.getDiet());
    }

    // ── BMI UI ────────────────────────────────────────────────────────────
    private void updateBmiUI(double bmi) {
        // Show rounded BMI value
        String bmiText = String.format("%.1f", bmi);
        tvBmiValue.setText(bmiText);

        // Progress: map BMI 15–40 → 0–100%
        float minBmi = 15f, maxBmi = 40f;
        float progress = (float) ((bmi - minBmi) / (maxBmi - minBmi));
        progress = Math.max(0f, Math.min(1f, progress));

        final float finalProgress = progress;

        // Animate progress bar width
        viewBmiProgress.post(() -> {
            int parentWidth = ((View) viewBmiProgress.getParent()).getWidth();
            int fillWidth = (int) (parentWidth * finalProgress);
            ViewGroup.LayoutParams lp = viewBmiProgress.getLayoutParams();
            lp.width = fillWidth;
            viewBmiProgress.setLayoutParams(lp);

            // Position thumb
            FrameLayout.LayoutParams tp = (FrameLayout.LayoutParams) viewBmiThumb.getLayoutParams();
            tp.leftMargin = fillWidth - 9; // half of 18dp thumb
            viewBmiThumb.setLayoutParams(tp);
        });

        // Status message & color
        if (bmi < 18.5) {
            // LOW BMI
            tvBmiMessage.setText("Low BMI — Consider increasing calorie intake and strength training.");
            llBmiStatus.setBackgroundResource(R.drawable.bg_bmi_status_low);
            ivBmiIcon.setImageResource(R.drawable.ic_bmi_low);
            tvBmiValue.setBackgroundResource(R.drawable.bg_bmi_pill_low);
        } else if (bmi <= 24.9) {
            // HEALTHY BMI
            tvBmiMessage.setText("Healthy BMI — You're in great shape! Keep up the good work.");
            llBmiStatus.setBackgroundResource(R.drawable.bg_bmi_status_good);
            ivBmiIcon.setImageResource(R.drawable.ic_bmi_good);
            tvBmiValue.setBackgroundResource(R.drawable.bg_bmi_pill_good);
        } else {
            // HIGH BMI
            tvBmiMessage.setText("High BMI — Focus on cardio workouts and a balanced diet.");
            llBmiStatus.setBackgroundResource(R.drawable.bg_bmi_status_high);
            ivBmiIcon.setImageResource(R.drawable.ic_bmi_high);
            tvBmiValue.setBackgroundResource(R.drawable.bg_bmi_pill_high);
        }
    }

    // ── Helper: fill a detail row ─────────────────────────────────────────
    private void setRow(View row, String label, String value) {
        if (row == null) return;
        TextView tvLabel = row.findViewById(R.id.tvLabel);
        TextView tvValue = row.findViewById(R.id.tvValue);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value != null && !value.isEmpty() ? value : "—");
    }

    // ── Gallery / Upload ──────────────────────────────────────────────────
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void uploadProfilePic(Uri uri) {
        StorageReference ref = storage.getReference()
                .child("profile_pics/" + currentUid + ".jpg");
        ref.putFile(uri)
                .addOnSuccessListener(snap ->
                        ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String url = downloadUri.toString();
                            // Update Firestore
                            db.collection("users").document(currentUid)
                                    .update("profilePicUrl", url)
                                    .addOnSuccessListener(v -> {
                                        Glide.with(this)
                                                .load(url)
                                                .apply(new RequestOptions().circleCrop())
                                                .into(ivProfilePic);
                                        Toast.makeText(getContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show();
                                    });
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void openEditProfileActivity() {
        // Replace with your EditProfileActivity class
        // startActivity(new Intent(getContext(), EditProfileActivity.class));
        Toast.makeText(getContext(), "Edit Profile", Toast.LENGTH_SHORT).show();
    }

    // ── Sign Out ──────────────────────────────────────────────────────────
    private void signOut() {
        auth.signOut();
        // Navigate back to login, e.g.:
        // startActivity(new Intent(getContext(), LoginActivity.class));
        // requireActivity().finish();
    }


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}