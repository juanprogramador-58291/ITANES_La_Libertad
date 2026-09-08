package com.example.itanes_la_libertad.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.FavoriteEntity;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.FavoriteRepository;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "com.example.itanes_la_libertad.EXTRA_PLACE_ID";
    private PlaceRepository repository;
    private FavoriteRepository favoriteRepository;

    private ImageView imageDetailPlace;
    private TextView textDetailName;
    private TextView textDetailShortDescription;
    private TextView textDetailDescription;
    private TextView textDetailAddress;
    private TextView textDetailLat;
    private TextView textDetailLng;
    private Button buttonFavorite;
    private Button buttonShare;

    private boolean isFavorite = false;
    private int currentPlaceId = -1;
    private PlaceEntity currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        initViews();
        repository = new PlaceRepository(getApplication());
        favoriteRepository = new FavoriteRepository(getApplication());

        currentPlaceId = getIntent().getIntExtra(EXTRA_PLACE_ID, -1);

        if (currentPlaceId == -1) {
            Toast.makeText(this, R.string.error_invalid_id, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaceDetail(currentPlaceId);
        checkFavoriteStatus(currentPlaceId);
    }

    private void initViews() {
        imageDetailPlace = findViewById(R.id.imageDetailPlace);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailShortDescription = findViewById(R.id.textDetailShortDescription);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        textDetailAddress = findViewById(R.id.textDetailAddress);
        textDetailLat = findViewById(R.id.textDetailLat);
        textDetailLng = findViewById(R.id.textDetailLng);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonShare = findViewById(R.id.buttonShare);
        
        buttonFavorite.setOnClickListener(v -> toggleFavorite());
        buttonShare.setOnClickListener(v -> sharePlace());
        
        // Botón visual (sin funcionalidad aún)
        findViewById(R.id.buttonMap);
    }

    private void sharePlace() {
        if (currentPlace == null) return;

        String shareText = getString(R.string.share_template,
                currentPlace.getName(),
                currentPlace.getShortDescription(),
                currentPlace.getAddress(),
                getString(R.string.app_name));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentPlace.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFavoriteStatus(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            isFavorite = favoriteRepository.isFavorite(id);
            runOnUiThread(this::updateFavoriteButton);
        });
    }

    private void toggleFavorite() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (isFavorite) {
                favoriteRepository.deleteByPlaceId(currentPlaceId);
                isFavorite = false;
            } else {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                favoriteRepository.insert(new FavoriteEntity(currentPlaceId, timestamp));
                isFavorite = true;
            }
            runOnUiThread(this::updateFavoriteButton);
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            buttonFavorite.setText(R.string.button_favorite_remove);
        } else {
            buttonFavorite.setText(R.string.button_favorite);
        }
    }

    private void loadPlaceDetail(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PlaceEntity place = repository.getPlaceById(id);
            runOnUiThread(() -> {
                if (place != null) {
                    currentPlace = place;
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

        Glide.with(this)
                .load(place.getImageUrl())
                .placeholder(R.drawable.ic_place_placeholder)
                .error(R.drawable.ic_error_image)
                .centerCrop()
                .into(imageDetailPlace);
    }
}
