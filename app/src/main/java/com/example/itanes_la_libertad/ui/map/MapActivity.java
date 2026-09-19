package com.example.itanes_la_libertad.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.detail.PlaceDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.Style;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private TextView textMapPlaceName;
    private TextView textMapPlaceAddress;
    private MapLibreMap mapLibreMap;

    private PlaceRepository placeRepository;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inicializar MapLibre antes de inflar la vista
        MapLibre.getInstance(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        textMapPlaceName = findViewById(R.id.textMapPlaceName);
        textMapPlaceAddress = findViewById(R.id.textMapPlaceAddress);
        mapView = findViewById(R.id.mapView);

        ImageButton btnMapBack = findViewById(R.id.btnMapBack);
        btnMapBack.setOnClickListener(v -> finish());

        FloatingActionButton fabZoomIn = findViewById(R.id.fabZoomIn);
        FloatingActionButton fabZoomOut = findViewById(R.id.fabZoomOut);

        fabZoomIn.setOnClickListener(v -> performZoom(true));
        fabZoomOut.setOnClickListener(v -> performZoom(false));

        mapView.onCreate(savedInstanceState);

        placeRepository = new PlaceRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        int placeId = getIntent().getIntExtra(PlaceDetailActivity.EXTRA_PLACE_ID, -1);

        if (placeId <= 0) {
            Toast.makeText(this, getString(R.string.error_invalid_id), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadPlaceAndSetupMap(placeId);

        // Configuración de la Navegación Inferior (Requerimiento visual de la referencia)
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        android.view.View bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
            com.google.android.material.bottomnavigation.BottomNavigationView navigationView = (com.google.android.material.bottomnavigation.BottomNavigationView) bottomNav;
            navigationView.setSelectedItemId(R.id.nav_places); 
            navigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(this, com.example.itanes_la_libertad.MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_places) {
                    Intent intent = new Intent(this, com.example.itanes_la_libertad.PlacesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_favorites) {
                    Intent intent = new Intent(this, com.example.itanes_la_libertad.ui.favorites.FavoritesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        }
    }

    private void loadPlaceAndSetupMap(int placeId) {
        executorService.execute(() -> {
            final PlaceEntity place = placeRepository.getPlaceById(placeId);

            mainHandler.post(() -> {
                if (place != null) {
                    setupMapWithLocation(place);
                } else {
                    Toast.makeText(MapActivity.this, getString(R.string.error_place_not_found), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });
    }

    private void setupMapWithLocation(PlaceEntity place) {
        double lat = place.getLatitude();
        double lon = place.getLongitude();

        // Validar coordenadas usando el centralizado LocationValidator
        if (!com.example.itanes_la_libertad.LocationValidator.isValid(lat, lon)) {
            Toast.makeText(this, getString(R.string.error_invalid_coordinates), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        textMapPlaceName.setText(place.getName());
        textMapPlaceAddress.setText(place.getAddress());

        mapView.getMapAsync(map -> {
            this.mapLibreMap = map;
            map.setStyle(new Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"), style -> {
                LatLng location = new LatLng(lat, lon);
                
                // Centrar la camara con zoom apropiado (15)
                CameraPosition position = new CameraPosition.Builder()
                        .target(location)
                        .zoom(15.0)
                        .build();
                
                map.moveCamera(CameraUpdateFactory.newCameraPosition(position));

                // Agregar el marcador con el nombre del punto turístico
                map.addMarker(new MarkerOptions()
                        .position(location)
                        .title(place.getName()));
            });
        });
    }

    private void performZoom(boolean zoomIn) {
        if (mapLibreMap != null) {
            double currentZoom = mapLibreMap.getCameraPosition().zoom;
            double newZoom = zoomIn ? currentZoom + 1.0 : currentZoom - 1.0;
            
            // Límites de zoom razonables (3 a 19)
            if (newZoom >= 3.0 && newZoom <= 19.0) {
                mapLibreMap.animateCamera(CameraUpdateFactory.zoomTo(newZoom));
            }
        }
    }

    // Gestionar el ciclo de vida de MapView
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
        if (executorService != null) {
            executorService.shutdown();
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
