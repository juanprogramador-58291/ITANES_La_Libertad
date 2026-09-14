package com.example.itanes_la_libertad.data.repository;

import android.content.Context;

import com.example.itanes_la_libertad.data.local.dao.PlaceDao;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;

import java.util.List;

public class PlaceRepository {

    private final PlaceDao placeDao;

    public PlaceRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.placeDao = database.placeDao();
    }

    public PlaceRepository(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    public void insertPlaces(List<PlaceEntity> places) {
        placeDao.insertPlaces(places);
    }

    public void insertPlace(PlaceEntity place) {
        placeDao.insertPlace(place);
    }

    public List<PlaceEntity> getAllPlaces() {
        return placeDao.getAllPlaces();
    }

    public PlaceEntity getPlaceById(int id) {
        return placeDao.getPlaceById(id);
    }

    public void deleteAllPlaces() {
        placeDao.deleteAllPlaces();
    }

    public int getPlacesCount() {
        return placeDao.getPlacesCount();
    }
}
