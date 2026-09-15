package com.example.itanes_la_libertad.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itanes_la_libertad.MainActivity;
import com.example.itanes_la_libertad.PlacesActivity;
import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.FavoriteRepository;
import com.example.itanes_la_libertad.ui.adapter.PlaceAdapter;
import com.example.itanes_la_libertad.ui.detail.PlaceDetailActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private LinearLayout layoutEmptyState;
    private PlaceAdapter placeAdapter;
    private FavoriteRepository favoriteRepository;
    private ExecutorService executorService;
    private Handler mainHandler;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        placeAdapter = new PlaceAdapter(new ArrayList<>(), placeId -> {
            Intent intent = new Intent(FavoritesActivity.this, PlaceDetailActivity.class);
            intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, placeId);
            startActivity(intent);
        });
        recyclerViewFavorites.setAdapter(placeAdapter);

        favoriteRepository = new FavoriteRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_places) {
                Intent intent = new Intent(this, PlacesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_favorites);
        }
        loadFavoritesFromRoom();
    }

    private void loadFavoritesFromRoom() {
        executorService.execute(() -> {
            final List<PlaceEntity> favoritePlaces = favoriteRepository.getFavoritePlaces();
            mainHandler.post(() -> {
                if (favoritePlaces == null || favoritePlaces.isEmpty()) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerViewFavorites.setVisibility(View.GONE);
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    recyclerViewFavorites.setVisibility(View.VISIBLE);
                    placeAdapter.updateList(favoritePlaces);
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
