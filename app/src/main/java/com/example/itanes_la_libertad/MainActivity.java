package com.example.itanes_la_libertad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itanes_la_libertad.data.local.seed.PlaceDataSeeder;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;

public class MainActivity extends AppCompatActivity {

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
    }
}
