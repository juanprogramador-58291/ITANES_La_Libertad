package com.example.itanes_la_libertad.data.repository;

import android.app.Application;

import com.example.itanes_la_libertad.data.local.dao.FavoriteDao;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.FavoriteEntity;

import java.util.List;

public class FavoriteRepository {

    private final FavoriteDao favoriteDao;

    public FavoriteRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        favoriteDao = db.favoriteDao();
    }

    public void insert(FavoriteEntity favorite) {
        favoriteDao.insert(favorite);
    }

    public void deleteByPlaceId(int placeId) {
        favoriteDao.deleteByPlaceId(placeId);
    }

    public boolean isFavorite(int placeId) {
        return favoriteDao.isFavorite(placeId);
    }

    public List<Integer> getFavoriteIds() {
        return favoriteDao.getFavoriteIds();
    }
}
