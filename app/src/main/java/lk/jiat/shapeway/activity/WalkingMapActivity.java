package lk.jiat.shapeway.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.databinding.ActivityWalkingMapBinding;
import lk.jiat.shapeway.network.DirectionApi;
import lk.jiat.shapeway.walk.WalkPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalkingMapActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;
    private static final int ACTIVITY_PERMISSION_REQUEST_CODE = 201;

    private GoogleMap mMap;
    private ActivityWalkingMapBinding binding;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private Polyline polyline;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private WalkPrefs walkPrefs;

    private int dailyTarget;
    private int todaySteps;
    private int remainingSteps;

    private static final double STEP_LENGTH_METERS = 0.75; // average walking step length

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalkingMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        walkPrefs = new WalkPrefs(this);
        walkPrefs.ensureTodayReset();

        dailyTarget = walkPrefs.getDailyTarget();
        todaySteps = walkPrefs.getTodaySteps();
        remainingSteps = walkPrefs.getRemainingSteps();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        setupUi();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupUi() {
        updateProgressUi();

        binding.btnRefreshRoute.setOnClickListener(v -> {
            if (currentLocation == null) {
                Toast.makeText(this, "Current location not ready yet", Toast.LENGTH_SHORT).show();
                return;
            }

            remainingSteps = walkPrefs.getRemainingSteps();
            if (remainingSteps <= 0) {
                Toast.makeText(this, "Today's target completed", Toast.LENGTH_SHORT).show();
                return;
            }

            generateRouteForRemainingSteps();
        });

        binding.btnEndSession.setOnClickListener(v -> finish());
    }

    private void updateProgressUi() {
        todaySteps = walkPrefs.getTodaySteps();
        remainingSteps = walkPrefs.getRemainingSteps();

        binding.tvMapCovered.setText("Covered today: " + todaySteps + " steps");
        binding.tvMapRemaining.setText("Remaining: " + remainingSteps + " steps");

        binding.mapProgress.setMax(dailyTarget);
        binding.mapProgress.setProgress(Math.min(todaySteps, dailyTarget));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        checkPermissionsAndStart();
    }

    private void checkPermissionsAndStart() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startFeatures();
        }
    }

    private void startFeatures() {
        enableMyLocation();
        startStepCounting();
    }

    private void startStepCounting() {
        if (stepCounterSensor == null) {
            Toast.makeText(this, "Step sensor not available on this device", Toast.LENGTH_LONG).show();
            return;
        }

        walkPrefs.setSessionRunning(true);
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void stopStepCounting() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        walkPrefs.setSessionRunning(false);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            startLocationUpdate();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f));
                    }

                    if (polyline == null && walkPrefs.getRemainingSteps() > 0) {
                        generateRouteForRemainingSteps();
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void generateRouteForRemainingSteps() {
        if (currentLocation == null) return;

        int remain = walkPrefs.getRemainingSteps();
        double targetMeters = remain * STEP_LENGTH_METERS;

        LatLng destination = createDestinationFromDistance(currentLocation, targetMeters / 2.0);
        requestWalkingRoute(currentLocation, destination);
    }

    private LatLng createDestinationFromDistance(LatLng start, double meters) {
        double earthRadius = 6378137.0;
        double bearing = Math.toRadians(90); // east, you can randomize if needed

        double lat1 = Math.toRadians(start.latitude);
        double lon1 = Math.toRadians(start.longitude);
        double angularDistance = meters / earthRadius;

        double lat2 = Math.asin(
                Math.sin(lat1) * Math.cos(angularDistance) +
                        Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(bearing)
        );

        double lon2 = lon1 + Math.atan2(
                Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(lat1),
                Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2)
        );

        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    private void requestWalkingRoute(LatLng start, LatLng end) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionApi directionApi = retrofit.create(DirectionApi.class);

        String origin = start.latitude + "," + start.longitude;
        String destination = end.latitude + "," + end.longitude;

        Call<JsonObject> call = directionApi.getJson(
                origin,
                destination,
                "walking",
                true,
                getString(R.string.google_maps_key)
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(WalkingMapActivity.this, "Failed to load route", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject body = response.body();
                JsonArray routes = body.getAsJsonArray("routes");

                if (routes == null || routes.size() == 0) {
                    Toast.makeText(WalkingMapActivity.this, "No walking route found", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject route = routes.get(0).getAsJsonObject();
                JsonObject overviewPolyline = route.getAsJsonObject("overview_polyline");

                List<LatLng> points = PolyUtil.decode(overviewPolyline.get("points").getAsString());

                if (polyline == null) {
                    polyline = mMap.addPolyline(
                            new PolylineOptions()
                                    .addAll(points)
                                    .width(18f)
                                    .color(getColor(R.color.colorStartWorkout))
                    );
                } else {
                    polyline.setPoints(points);
                }

                LatLng lastPoint = points.get(points.size() - 1);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(start).title("Start"));
                mMap.addMarker(new MarkerOptions().position(lastPoint).title("Target Route"));
                polyline = mMap.addPolyline(
                        new PolylineOptions()
                                .addAll(points)
                                .width(18f)
                                .color(getColor(R.color.colorStartWorkout))
                );
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(WalkingMapActivity.this, "Route error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_STEP_COUNTER) return;

        float totalSinceBoot = event.values[0];
        float savedBase = walkPrefs.getSensorBase();

        if (savedBase < 0) {
            walkPrefs.setSensorBase(totalSinceBoot);
            return;
        }

        int sessionSteps = (int) (totalSinceBoot - savedBase);
        if (sessionSteps < 0) sessionSteps = 0;

        walkPrefs.setTodaySteps(sessionSteps);
        updateProgressUi();

        if (walkPrefs.getRemainingSteps() <= 0) {
            Toast.makeText(this, "Congratulations! Daily target completed.", Toast.LENGTH_LONG).show();
            stopStepCounting();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (stepCounterSensor != null) {
            startStepCounting();
        }

        if (mMap != null &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdate();
        }

        updateProgressUi();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        stopStepCounting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStepCounting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean granted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && granted) {
            startFeatures();
        } else {
            Toast.makeText(this, "Required permissions denied", Toast.LENGTH_SHORT).show();
        }
    }
}