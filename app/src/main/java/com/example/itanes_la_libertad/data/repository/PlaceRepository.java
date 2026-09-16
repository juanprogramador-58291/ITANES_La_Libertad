package com.example.itanes_la_libertad.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.itanes_la_libertad.data.local.dao.PlaceDao;
import com.example.itanes_la_libertad.data.local.database.AppDatabase;
import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.mapper.PlaceMapper;
import com.example.itanes_la_libertad.data.remote.api.ItanesApiService;
import com.example.itanes_la_libertad.data.remote.dto.PlaceRemoteDto;
import com.example.itanes_la_libertad.data.remote.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceRepository {

    private static final String SYNC_TAG = "ITANES_SYNC";
    private final PlaceDao placeDao;
    private final ExecutorService executorService;

    public PlaceRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.placeDao = database.placeDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public PlaceRepository(PlaceDao placeDao) {
        this.placeDao = placeDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insertPlaces(List<PlaceEntity> places) {
        try {
            placeDao.insertPlaces(places);
        } catch (Exception e) {
            Log.e("ITANES_ROOM", "Error al insertar lugares: " + e.getMessage(), e);
        }
    }

    public void insertPlace(PlaceEntity place) {
        try {
            placeDao.insertPlace(place);
        } catch (Exception e) {
            Log.e("ITANES_ROOM", "Error al insertar lugar: " + e.getMessage(), e);
        }
    }

    public List<PlaceEntity> getAllPlaces() {
        try {
            return placeDao.getAllPlaces();
        } catch (Exception e) {
            Log.e("ITANES_ROOM", "Error al obtener todos los lugares: " + e.getMessage(), e);
            return null;
        }
    }

    public PlaceEntity getPlaceById(int id) {
        try {
            return placeDao.getPlaceById(id);
        } catch (Exception e) {
            Log.e("ITANES_ROOM", "Error al obtener lugar por id: " + e.getMessage(), e);
            return null;
        }
    }

    public void deleteAllPlaces() {
        try {
            placeDao.deleteAllPlaces();
        } catch (Exception e) {
            Log.e("ITANES_ROOM", "Error al borrar todos los lugares: " + e.getMessage(), e);
        }
    }

    public int getPlacesCount() {
        try {
            return placeDao.getPlacesCount();
        } catch (Exception e) {
            Log.e("ITANES_ROOM", "Error al obtener conteo de lugares: " + e.getMessage(), e);
            return 0;
        }
    }

    public void syncPlaces(OnSyncCompleteListener listener) {
        Log.i(SYNC_TAG, "ITANES_SYNC: Iniciando sincronización");
        ItanesApiService apiService = RetrofitClient.getApiService();
        Call<List<PlaceRemoteDto>> call = apiService.getPlaces();

        call.enqueue(new Callback<List<PlaceRemoteDto>>() {
            @Override
            public void onResponse(Call<List<PlaceRemoteDto>> call, Response<List<PlaceRemoteDto>> response) {
                if (response.isSuccessful()) {
                    List<PlaceRemoteDto> remotePlaces = response.body();
                    if (remotePlaces != null && !remotePlaces.isEmpty()) {
                        Log.i(SYNC_TAG, "ITANES_SYNC: " + remotePlaces.size() + " lugares recibidos");

                        executorService.execute(() -> {
                            try {
                                List<PlaceEntity> placesEntities = PlaceMapper.toEntityList(remotePlaces);
                                placeDao.insertPlaces(placesEntities);
                                Log.i(SYNC_TAG, "ITANES_SYNC: " + placesEntities.size() + " lugares guardados en Room");
                                Log.i(SYNC_TAG, "ITANES_SYNC: Sincronización completada");
                                
                                if (listener != null) {
                                    listener.onSyncSuccess();
                                }
                            } catch (Exception e) {
                                Log.e("ITANES_ROOM", "ITANES_SYNC: Excepción al guardar en Room: " + e.getMessage(), e);
                                if (listener != null) {
                                    listener.onSyncFailure(e.getMessage());
                                }
                            }
                        });
                    } else {
                        Log.i(SYNC_TAG, "ITANES_SYNC: Respuesta vacía, se conservan datos locales");
                        if (listener != null) {
                            listener.onSyncSuccess();
                        }
                    }
                } else {
                    Log.e(SYNC_TAG, "ITANES_SYNC: Error HTTP " + response.code());
                    if (listener != null) {
                        listener.onSyncFailure("Error HTTP " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PlaceRemoteDto>> call, Throwable t) {
                Log.e(SYNC_TAG, "ITANES_SYNC: Error de red / timeout: " + t.getMessage(), t);
                if (listener != null) {
                    listener.onSyncFailure(t.getMessage());
                }
            }
        });
    }

    public interface OnSyncCompleteListener {
        void onSyncSuccess();
        void onSyncFailure(String error);
    }
}
