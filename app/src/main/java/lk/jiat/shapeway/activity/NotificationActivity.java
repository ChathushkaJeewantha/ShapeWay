package lk.jiat.shapeway.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.adapters.NotificationAdapter;
import lk.jiat.shapeway.models.NotificationModel;

public class NotificationActivity extends AppCompatActivity {

    private MaterialToolbar toolbarNotification;
    private RecyclerView recyclerNotifications;
    private TextView txtEmptyNotifications;

    private FirebaseFirestore firestore;
    private final List<NotificationModel> notificationList = new ArrayList<>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbarNotification = findViewById(R.id.toolbarNotification);
        recyclerNotifications = findViewById(R.id.recyclerNotifications);
        txtEmptyNotifications = findViewById(R.id.txtEmptyNotifications);

        firestore = FirebaseFirestore.getInstance();

        toolbarNotification.setNavigationOnClickListener(v -> onBackPressed());

        adapter = new NotificationAdapter(this, notificationList);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        firestore.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        NotificationModel model = doc.toObject(NotificationModel.class);
                        model.setId(doc.getId());
                        notificationList.add(model);
                    }

                    adapter.notifyDataSetChanged();

                    if (notificationList.isEmpty()) {
                        txtEmptyNotifications.setVisibility(View.VISIBLE);
                        recyclerNotifications.setVisibility(View.GONE);
                    } else {
                        txtEmptyNotifications.setVisibility(View.GONE);
                        recyclerNotifications.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load notifications: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}