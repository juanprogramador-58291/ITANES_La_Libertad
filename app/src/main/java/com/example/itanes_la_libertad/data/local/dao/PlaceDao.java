package com.example.itanes_la_libertad.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

import java.util.List;

@Dao
public interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlaceEntity> places);

    @Query("SELECT * FROM places ORDER BY orderNumber ASC")
    List<PlaceEntity> getAllPlaces();

    @Query("SELECT * FROM places WHERE id = :id")
    PlaceEntity getPlaceById(int id);

    @Query("SELECT COUNT(*) FROM places")
    int getCount();

    @Query("DELETE FROM places")
    void deleteAll();
}
