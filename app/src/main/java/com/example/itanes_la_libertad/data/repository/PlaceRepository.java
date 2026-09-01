package com.example.itanes_la_libertad.data.repository;

import android.app.Application;

import com.example.itanes_la_libertad.data.local.dao.PlaceDao;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

import java.util.List;

public class PlaceRepository {

    private final PlaceDao placeDao;

    public PlaceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        placeDao = db.placeDao();
    }

    public List<PlaceEntity> getAllPlaces() {
        return placeDao.getAllPlaces();
    }

    public PlaceEntity getPlaceById(int id) {
        return placeDao.getPlaceById(id);
    }

    public int getCount() {
        return placeDao.getCount();
    }

    public void insertAll(List<PlaceEntity> places) {
        placeDao.insertAll(places);
    }

    public void deleteAll() {
        placeDao.deleteAll();
    }
}
