package com.example.itanes_la_libertad.data.repository;

import android.content.Context;

import com.example.itanes_la_libertad.data.local.dao.FavoriteDao;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.FavoriteEntity;

public class FavoriteRepository {

    private final FavoriteDao favoriteDao;

    public FavoriteRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.favoriteDao = database.favoriteDao();
    }

    public void insertFavorite(FavoriteEntity favorite) {
        favoriteDao.insertFavorite(favorite);
    }

    public void deleteFavoriteById(int placeId) {
        favoriteDao.deleteFavoriteById(placeId);
    }

    public boolean isFavorite(int placeId) {
        return favoriteDao.isFavorite(placeId) > 0;
    }
}
