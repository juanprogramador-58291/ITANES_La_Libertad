package com.example.itanes_la_libertad.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.itanes_la_libertad.data.local.entity.FavoriteEntity;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    // 1. Insertar un favorito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteEntity favorite);

    // 2. Eliminar favorito mediante placeId
    @Query("DELETE FROM favorites WHERE placeId = :placeId")
    void deleteFavoriteById(int placeId);

    // 3. Verificar si un placeId es favorito (devuelve 1 si es verdadero, 0 si no)
    @Query("SELECT COUNT(*) FROM favorites WHERE placeId = :placeId")
    int isFavorite(int placeId);

    // 4. Obtener todos los IDs de lugares favoritos
    @Query("SELECT placeId FROM favorites")
    List<Integer> getAllFavoriteIds();

    // 5. Obtener todos los lugares turísticos marcados como favoritos usando un SQL JOIN
    @Query("SELECT places.* FROM places INNER JOIN favorites ON places.id = favorites.placeId ORDER BY favorites.createdAt DESC")
    List<com.example.itanes_la_libertad.data.local.entity.PlaceEntity> getFavoritePlaces();
}
