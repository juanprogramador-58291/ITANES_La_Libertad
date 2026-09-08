package com.example.itanes_la_libertad;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itanes_la_libertad.adapter.PlaceAdapter;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.detail.PlaceDetailActivity;

import java.util.List;

public class PlacesActivity extends AppCompatActivity implements PlaceAdapter.OnPlaceClickListener {

    private PlaceRepository repository;
    private PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Toolbar toolbar = findViewById(R.id.toolbarPlaces);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerPlaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new PlaceAdapter(this);
        recyclerView.setAdapter(adapter);

        repository = new PlaceRepository(getApplication());

        loadPlaces();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadPlaces() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<PlaceEntity> places = repository.getAllPlaces();
            runOnUiThread(() -> {
                adapter.setPlaces(places);
            });
        });
    }

    @Override
    public void onPlaceClick(PlaceEntity place) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, place.getId());
        startActivity(intent);
    }
}
