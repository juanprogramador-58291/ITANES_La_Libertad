package com.example.itanes_la_libertad.data.local.seed;

import android.util.Log;

import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

public class PlaceDataSeeder {

    private static final String TAG = "PlaceDataSeeder";

    public static void seed(PlaceRepository repository) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (repository.getCount() == 0) {
                Log.d(TAG, "La tabla places está vacía. Iniciando carga de datos...");
                repository.insertAll(getInitialPlaces());

                // Consulta dinámica después de la inserción
                int finalCount = repository.getCount();
                Log.d(TAG, "PlaceDataSeeder: " + finalCount + " lugares disponibles en Room");
            } else {
                Log.d(TAG, "La tabla places ya contiene datos. Se omite el seeder.");
            }
        });
    }

    private static List<PlaceEntity> getInitialPlaces() {
        List<PlaceEntity> places = new ArrayList<>();

        places.add(new PlaceEntity(
                1,
                "Plaza Mayor de Trujillo",
                "Corazón histórico y cívico de Trujillo.",
                "Es el espacio público principal y uno de los lugares más representativos del Centro Histórico de Trujillo. Se remonta a la fundación de la ciudad y está rodeada por importantes edificios históricos. En su centro se encuentra el Monumento a la Libertad, convertido en uno de los símbolos urbanos de Trujillo.",
                "Centro Histórico de Trujillo; delimitada por los jirones Independencia, Orbegoso, Pizarro y Almagro.",
                -8.11181,
                -79.02867,
                "https://commons.wikimedia.org/wiki/Special:Redirect/file/Plaza%20de%20Armas%20de%20Trujillo.jpg?width=640",
                1,
                "2026-09-01T10:00:00Z"
        ));

        places.add(new PlaceEntity(
                2,
                "Basílica Catedral de Santa María",
                "Principal templo católico de la ciudad de Trujillo.",
                "La Catedral de Trujillo es uno de los principales monumentos religiosos del centro histórico. El edificio actual fue levantado en el siglo XVII y destaca por su importancia religiosa, histórica y arquitectónica. Se encuentra junto a la Plaza Mayor, por lo que forma parte natural de un recorrido peatonal por el centro.",
                "Jirón Orbegoso 451, Centro Histórico, Trujillo 13001.",
                -8.1114,
                -79.0282,
                "https://commons.wikimedia.org/wiki/Special:Redirect/file/Catedral%20de%20Trujillo%20-%2043.jpg?width=640",
                2,
                "2026-09-01T10:00:00Z"
        ));

        places.add(new PlaceEntity(
                3,
                "Casa Urquiaga (Casa Calonge)",
                "Casona histórica y museo junto a la Plaza Mayor.",
                "Casona histórica de origen colonial y apariencia neoclásica, actualmente vinculada al Banco Central de Reserva del Perú. Conserva mobiliario y objetos de distintas etapas de la historia peruana y está asociada a la estancia de Simón Bolívar en Trujillo.",
                "Jirón Francisco Pizarro 446, Centro Histórico, Trujillo 13001.",
                -8.11247,
                -79.02833,
                "https://commons.wikimedia.org/wiki/Special:Redirect/file/PE%20Trujillo%209412%20002.jpg?width=640",
                3,
                "2026-09-01T10:00:00Z"
        ));

        places.add(new PlaceEntity(
                4,
                "Palacio Iturregui (Club Central)",
                "Destacada casona neoclásica del Centro Histórico.",
                "El Palacio Iturregui es una de las construcciones civiles más representativas de Trujillo y destaca por su arquitectura neoclásica. Su fachada, patios y composición monumental lo convierten en un punto importante para comprender el patrimonio arquitectónico de la ciudad.",
                "Jirón Junín 682, Centro Histórico, Trujillo.",
                -8.10967,
                -79.02573,
                "https://commons.wikimedia.org/wiki/Special:Redirect/file/Palacio%20Iturregui%20-%20Fachada.jpg?width=640",
                4,
                "2026-09-01T10:00:00Z"
        ));

        places.add(new PlaceEntity(
                5,
                "Plazuela El Recreo",
                "Plaza tradicional con patrimonio urbano y ambiente cultural.",
                "La Plazuela El Recreo es un espacio tradicional del Centro Histórico de Trujillo. Conserva una fuente de mármol y elementos vinculados con la antigua infraestructura de abastecimiento de agua de la ciudad. También funciona como espacio de encuentro y escenario de actividades culturales.",
                "Jirón Francisco Pizarro 900 / cuadra 9, Centro Histórico, Trujillo.",
                -8.10712,
                -79.02369,
                "https://commons.wikimedia.org/wiki/Special:Redirect/file/Plazuela%20El%20Recreo%20-%20Panor%C3%A1mica.jpg?width=640",
                5,
                "2026-09-01T10:00:00Z"
        ));

        return places;
    }
}
