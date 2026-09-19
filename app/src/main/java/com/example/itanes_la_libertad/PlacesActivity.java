package com.example.itanes_la_libertad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.adapter.PlaceAdapter;
import com.example.itanes_la_libertad.ui.detail.PlaceDetailActivity;
import com.example.itanes_la_libertad.ui.favorites.FavoritesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlacesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPlaces;
    private PlaceAdapter placeAdapter;
    private PlaceRepository placeRepository;
    private ExecutorService executorService;
    private Handler mainHandler;
    private BottomNavigationView bottomNavigationView;
    
    private android.widget.ProgressBar progressBarPlaces;
    private android.widget.LinearLayout layoutEmptyPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        
        recyclerViewPlaces = findViewById(R.id.recyclerViewPlaces);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        progressBarPlaces = findViewById(R.id.progressBarPlaces);
        layoutEmptyPlaces = findViewById(R.id.layoutEmptyPlaces);
        
        placeAdapter = new PlaceAdapter(new ArrayList<>(), placeId -> {
            Intent intent = new Intent(PlacesActivity.this, PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, placeId);
            startActivity(intent);
        });
        recyclerViewPlaces.setAdapter(placeAdapter);

        placeRepository = new PlaceRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        setupBottomNavigation();
        loadPlacesFromRoom();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_places);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_places) {
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

    private void loadPlacesFromRoom() {
        android.util.Log.d("ITANES_UI", "Iniciando carga de lugares desde Room");
        if (progressBarPlaces != null) progressBarPlaces.setVisibility(android.view.View.VISIBLE);
        if (recyclerViewPlaces != null) recyclerViewPlaces.setVisibility(android.view.View.GONE);
        if (layoutEmptyPlaces != null) layoutEmptyPlaces.setVisibility(android.view.View.GONE);

        executorService.execute(() -> {
            // Consulta a Room fuera del hilo principal
            final List<PlaceEntity> placesList = placeRepository.getAllPlaces();
            
            // Actualizar la UI en el hilo principal
            mainHandler.post(() -> {
                if (progressBarPlaces != null) progressBarPlaces.setVisibility(android.view.View.GONE);
                
                if (placesList != null && !placesList.isEmpty()) {
                    if (recyclerViewPlaces != null) recyclerViewPlaces.setVisibility(android.view.View.VISIBLE);
                    if (layoutEmptyPlaces != null) layoutEmptyPlaces.setVisibility(android.view.View.GONE);
                    placeAdapter.updateList(placesList);
                } else {
                    if (recyclerViewPlaces != null) recyclerViewPlaces.setVisibility(android.view.View.GONE);
                    if (layoutEmptyPlaces != null) layoutEmptyPlaces.setVisibility(android.view.View.VISIBLE);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_places);
        }
        loadPlacesFromRoom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
