package com.example.itanes_la_libertad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itanes_la_libertad.data.local.seed.PlaceDataSeeder;
import com.example.itanes_la_libertad.data.remote.api.ItanesApiService;
import com.example.itanes_la_libertad.data.remote.dto.PlaceRemoteDto;
import com.example.itanes_la_libertad.data.remote.retrofit.RetrofitClient;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ITANES_API";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar repositorio y sembrar datos si es necesario
        PlaceRepository repository = new PlaceRepository(getApplication());
        PlaceDataSeeder.seed(repository);

        // Configurar botón para explorar recorrido
        Button buttonExplore = findViewById(R.id.buttonExplore);
        buttonExplore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
            startActivity(intent);
        });

        // Prueba de consumo de API
        testApiConsumption();
    }

    private void testApiConsumption() {
        ItanesApiService apiService = RetrofitClient.getApiService();
        Call<List<PlaceRemoteDto>> call = apiService.getPlaces();

        call.enqueue(new Callback<List<PlaceRemoteDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PlaceRemoteDto>> call, @NonNull Response<List<PlaceRemoteDto>> response) {
                if (response.isSuccessful()) {
                    List<PlaceRemoteDto> places = response.body();
                    if (places != null && !places.isEmpty()) {
                        Log.d(TAG, "Respuesta recibida correctamente");
                        Log.d(TAG, "Total lugares: " + places.size());
                        for (PlaceRemoteDto place : places) {
                            Log.d(TAG, place.getId() + " - " + place.getName());
                        }
                    } else {
                        Log.w(TAG, "Respuesta vacía o nula");
                    }
                } else {
                    Log.e(TAG, "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PlaceRemoteDto>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red/conexión: " + t.getMessage());
            }
        });
    }
}
