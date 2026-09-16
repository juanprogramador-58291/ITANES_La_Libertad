package com.example.itanes_la_libertad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.itanes_la_libertad.data.local.seed.PlaceDataSeeder;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.favorites.FavoritesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización e inserción mínima de datos mediante el Seeder si la BD está vacía
        PlaceDataSeeder seeder = new PlaceDataSeeder(this);
        seeder.seedInitialData();

        // Disparar la sincronización simple al iniciar la aplicación a través del Repository
        PlaceRepository placeRepository = new PlaceRepository(this);
        placeRepository.syncPlaces(new PlaceRepository.OnSyncCompleteListener() {
            @Override
            public void onSyncSuccess() {
                // Sincronización silenciosa completada con éxito
            }

            @Override
            public void onSyncFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, getString(R.string.sync_offline_message), Toast.LENGTH_LONG).show();
                });
            }
        });

        // Configuración de la Navegación Inferior
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();

        // Navegación desde MainActivity a PlacesActivity
        Button buttonExplore = findViewById(R.id.buttonExplore);
        buttonExplore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_places) {
                Intent intent = new Intent(this, PlacesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(this, FavoritesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}

