package lk.jiat.shapeway.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNav = binding.bottomNavigation;

       MaterialToolbar quizToolbar = findViewById(R.id.quizToolbar);

        quizToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_logout) {
                signOutUser();
                return true;
            } else if (id == R.id.menu_noty) {
                nofiaction();
                return true;
            }

            return false;
        });


        // Load default fragment
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new Plan())
//                    .commit();
//        }

        loadFragment(new Plan());

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_plan) {
                fragment = new Plan();
            } else if (item.getItemId() == R.id.nav_workouts) {
                fragment = new WorkoutsFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                fragment = new Profile();
            } else if (item.getItemId() == R.id.nav_diet) {
                fragment = new DietFragment();
            }

            return loadFragment(fragment);
        });


    }





    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    private void nofiaction() {

        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);

    }

    private void signOutUser() {

        // Firebase Sign Out
        FirebaseAuth.getInstance().signOut();

        // Optional: Clear any saved user data (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Show message
        Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();

        // Go to SignInActivity
        Intent intent = new Intent(this, SignIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}