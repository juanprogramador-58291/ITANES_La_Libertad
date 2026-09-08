package com.example.itanes_la_libertad.ui.detail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "com.example.itanes_la_libertad.EXTRA_PLACE_ID";
    private PlaceRepository repository;

    private ImageView imageDetailPlace;
    private TextView textDetailName;
    private TextView textDetailShortDescription;
    private TextView textDetailDescription;
    private TextView textDetailAddress;
    private TextView textDetailLat;
    private TextView textDetailLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        initViews();
        repository = new PlaceRepository(getApplication());

        int placeId = getIntent().getIntExtra(EXTRA_PLACE_ID, -1);

        if (placeId == -1) {
            Toast.makeText(this, R.string.error_invalid_id, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaceDetail(placeId);
    }

    private void initViews() {
        imageDetailPlace = findViewById(R.id.imageDetailPlace);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailShortDescription = findViewById(R.id.textDetailShortDescription);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        textDetailAddress = findViewById(R.id.textDetailAddress);
        textDetailLat = findViewById(R.id.textDetailLat);
        textDetailLng = findViewById(R.id.textDetailLng);
        
        // Botones visuales (sin funcionalidad aún)
        findViewById(R.id.buttonFavorite);
        findViewById(R.id.buttonShare);
        findViewById(R.id.buttonMap);
    }

    private void loadPlaceDetail(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PlaceEntity place = repository.getPlaceById(id);
            runOnUiThread(() -> {
                if (place != null) {
                    displayPlace(place);
                } else {
                    Toast.makeText(this, R.string.error_place_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayPlace(PlaceEntity place) {
        textDetailName.setText(place.getName());
        textDetailShortDescription.setText(place.getShortDescription());
        textDetailDescription.setText(place.getDescription());
        textDetailAddress.setText(place.getAddress());
        textDetailLat.setText(String.valueOf(place.getLatitude()));
        textDetailLng.setText(String.valueOf(place.getLongitude()));
        imageDetailPlace.setImageResource(R.drawable.ic_place_placeholder);
    }
}
