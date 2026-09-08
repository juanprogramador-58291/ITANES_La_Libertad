package com.example.itanes_la_libertad.ui.map;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.detail.PlaceDetailActivity;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private PlaceRepository repository;
    private TextView textMapPlaceName;
    private PlaceEntity currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar MapLibre antes de setContentView
        MapLibre.getInstance(this);

        setContentView(R.layout.activity_map);

        textMapPlaceName = findViewById(R.id.textMapPlaceName);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        repository = new PlaceRepository(getApplication());

        int placeId = getIntent().getIntExtra(PlaceDetailActivity.EXTRA_PLACE_ID, -1);
        if (placeId == -1) {
            Toast.makeText(this, R.string.error_invalid_id, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaceData(placeId);
    }

    private void loadPlaceData(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PlaceEntity place = repository.getPlaceById(id);
            runOnUiThread(() -> {
                if (place != null) {
                    currentPlace = place;
                    textMapPlaceName.setText(place.getName());
                    if (mapLibreMap != null) {
                        setupMapLocation();
                    }
                } else {
                    Toast.makeText(this, R.string.error_place_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        this.mapLibreMap = mapLibreMap;
        
        mapLibreMap.setStyle(new Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"), style -> {
            if (currentPlace != null) {
                setupMapLocation();
            }
        });
    }

    private void setupMapLocation() {
        double lat = currentPlace.getLatitude();
        double lng = currentPlace.getLongitude();

        if (isValidCoordinate(lat, lng)) {
            LatLng location = new LatLng(lat, lng);
            
            // Agregar marcador
            mapLibreMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(currentPlace.getName()));

            // Centrar cámara
            CameraPosition position = new CameraPosition.Builder()
                    .target(location)
                    .zoom(14.0)
                    .build();
            
            mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
            Toast.makeText(this, R.string.error_invalid_coords, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidCoordinate(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
