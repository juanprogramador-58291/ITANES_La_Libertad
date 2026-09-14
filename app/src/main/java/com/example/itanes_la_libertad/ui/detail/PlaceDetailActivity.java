package com.example.itanes_la_libertad.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.itanes_la_libertad.R;
import com.example.itanes_la_libertad.data.local.entity.FavoriteEntity;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.FavoriteRepository;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;
import com.example.itanes_la_libertad.ui.map.MapActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "com.example.itanes_la_libertad.ui.detail.EXTRA_PLACE_ID";
    private static final int INVALID_ID = -1;

    private TextView textDetailName;
    private TextView textDetailShortDescription;
    private TextView textDetailDescription;
    private TextView textDetailAddress;
    private TextView textDetailCoordinates;
    private ImageView imageDetailPlace;
    private Button buttonFavorite;
    private Button buttonShare;
    private Button buttonViewMap;

    private PlaceRepository placeRepository;
    private FavoriteRepository favoriteRepository;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    private int placeId;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        textDetailName = findViewById(R.id.textDetailName);
        textDetailShortDescription = findViewById(R.id.textDetailShortDescription);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        textDetailAddress = findViewById(R.id.textDetailAddress);
        textDetailCoordinates = findViewById(R.id.textDetailCoordinates);
        imageDetailPlace = findViewById(R.id.imageDetailPlace);

        ImageButton btnDetailBack = findViewById(R.id.btnDetailBack);
        btnDetailBack.setOnClickListener(v -> finish());

        // Los botones requeridos visualmente (Tanda 06 no requiere funcionalidad)
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonShare = findViewById(R.id.buttonShare);
        buttonViewMap = findViewById(R.id.buttonViewMap);

        placeRepository = new PlaceRepository(this);
        favoriteRepository = new FavoriteRepository(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        placeId = getIntent().getIntExtra(EXTRA_PLACE_ID, INVALID_ID);

        if (placeId == INVALID_ID) {
            showErrorAndExit(getString(R.string.error_invalid_id));
            return;
        }

        // Configurar listener para el botón de favoritos
        buttonFavorite.setOnClickListener(v -> toggleFavorite());

        loadPlaceDetails(placeId);
    }

    private void loadPlaceDetails(int placeId) {
        executorService.execute(() -> {
            // Consulta de Room fuera del hilo principal usando el Repositorio
            final PlaceEntity place = placeRepository.getPlaceById(placeId);
            final boolean favStatus = favoriteRepository.isFavorite(placeId);

            mainHandler.post(() -> {
                if (place != null) {
                    isFavorite = favStatus;
                    updateFavoriteButtonVisuals();
                    populateUi(place);
                    
                    // Configurar la acción de compartir una vez cargado el objeto
                    buttonShare.setOnClickListener(v -> sharePlace(place));
                    
                    // Configurar la acción de abrir el mapa pasándole el ID
                    buttonViewMap.setOnClickListener(v -> {
                        Intent mapIntent = new Intent(PlaceDetailActivity.this, MapActivity.class);
                        mapIntent.putExtra(EXTRA_PLACE_ID, place.getId());
                        startActivity(mapIntent);
                    });
                } else {
                    showErrorAndExit(getString(R.string.error_place_not_found));
                }
            });
        });
    }

    private void toggleFavorite() {
        executorService.execute(() -> {
            if (isFavorite) {
                favoriteRepository.deleteFavoriteById(placeId);
            } else {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
                FavoriteEntity favorite = new FavoriteEntity(placeId, timestamp);
                favoriteRepository.insertFavorite(favorite);
            }
            
            // Invertir estado local y actualizar UI
            isFavorite = !isFavorite;
            
            mainHandler.post(this::updateFavoriteButtonVisuals);
        });
    }

    private void updateFavoriteButtonVisuals() {
        if (isFavorite) {
            buttonFavorite.setText(getString(R.string.btn_remove_favorite));
        } else {
            buttonFavorite.setText(getString(R.string.btn_favorite));
        }
    }

    private void populateUi(PlaceEntity place) {
        textDetailName.setText(place.getName());
        textDetailShortDescription.setText(place.getShortDescription());
        textDetailDescription.setText(place.getDescription());
        textDetailAddress.setText(place.getAddress());
        
        String coordinates = String.format(Locale.getDefault(), "Lat: %.5f, Lon: %.5f", 
                place.getLatitude(), place.getLongitude());
        textDetailCoordinates.setText(coordinates);

        Glide.with(this)
                .load(place.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .centerCrop()
                .into(imageDetailPlace);
    }

    private void sharePlace(PlaceEntity place) {
        try {
            String appName = getString(R.string.app_name);
            String footer = getString(R.string.share_template_footer, appName);
            
            String shareBody = place.getName() + "\n" +
                    place.getShortDescription() + "\n\n" +
                    getString(R.string.label_address) + "\n" +
                    place.getAddress() + "\n\n" +
                    footer;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, place.getName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.chooser_share_title));
            startActivity(chooserIntent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_no_share_app), Toast.LENGTH_LONG).show();
        }
    }

    private void showErrorAndExit(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
