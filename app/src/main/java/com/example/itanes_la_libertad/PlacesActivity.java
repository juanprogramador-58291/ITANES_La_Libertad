package com.example.itanes_la_libertad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;

import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.adapter.PlaceAdapter;
import com.example.itanes_la_libertad.ui.detail.PlaceDetailActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        recyclerViewPlaces = findViewById(R.id.recyclerViewPlaces);
        
        ImageButton btnPlacesBack = findViewById(R.id.btnPlacesBack);
        btnPlacesBack.setOnClickListener(v -> finish());
        
        placeAdapter = new PlaceAdapter(new ArrayList<>(), placeId -> {
            Intent intent = new Intent(PlacesActivity.this, PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, placeId);
            startActivity(intent);
        });
        recyclerViewPlaces.setAdapter(placeAdapter);

        placeRepository = new PlaceRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        loadPlacesFromRoom();
    }

    private void loadPlacesFromRoom() {
        executorService.execute(() -> {
            // Consulta a Room fuera del hilo principal
            final List<PlaceEntity> placesList = placeRepository.getAllPlaces();
            
            // Actualizar la UI en el hilo principal
            mainHandler.post(() -> {
                if (placesList != null) {
                    placeAdapter.updateList(placesList);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
