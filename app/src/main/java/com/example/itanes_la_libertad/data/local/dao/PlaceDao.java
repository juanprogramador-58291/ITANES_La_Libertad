package com.example.itanes_la_libertad.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

import java.util.List;

@Dao
public interface PlaceDao {

    // 1. Insertar uno o varios lugares reemplazando registros cuando exista el mismo ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaces(List<PlaceEntity> places);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlace(PlaceEntity place);

    // 2. Obtener todos los lugares ordenados por orderNumber de menor a mayor
    @Query("SELECT * FROM places ORDER BY orderNumber ASC")
    List<PlaceEntity> getAllPlaces();

    // 3. Obtener un lugar mediante su id
    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    PlaceEntity getPlaceById(int id);

    // 4. Eliminar todos los lugares
    @Query("DELETE FROM places")
    void deleteAllPlaces();

    // Obtener la cantidad total de lugares existentes
    @Query("SELECT COUNT(*) FROM places")
    int getPlacesCount();
}
