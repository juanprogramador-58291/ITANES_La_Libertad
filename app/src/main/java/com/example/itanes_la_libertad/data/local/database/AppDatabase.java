package com.example.itanes_la_libertad.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.itanes_la_libertad.data.local.dao.FavoriteDao;
import com.example.itanes_la_libertad.data.local.dao.PlaceDao;
import com.example.itanes_la_libertad.data.local.entity.FavoriteEntity;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

@Database(entities = {PlaceEntity.class, FavoriteEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract PlaceDao placeDao();
    public abstract FavoriteDao favoriteDao();

    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "itanes_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return instance;
    }
}
