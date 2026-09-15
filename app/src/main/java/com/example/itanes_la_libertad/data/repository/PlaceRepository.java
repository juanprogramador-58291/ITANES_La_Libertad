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

    public void syncPlaces(OnSyncCompleteListener listener) {
        Log.i(SYNC_TAG, "ITANES_SYNC: Iniciando sincronización");
        ItanesApiService apiService = RetrofitClient.getApiService();
        Call<List<PlaceRemoteDto>> call = apiService.getPlaces();

        call.enqueue(new Callback<List<PlaceRemoteDto>>() {
            @Override
            public void onResponse(Call<List<PlaceRemoteDto>> call, Response<List<PlaceRemoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaceRemoteDto> remotePlaces = response.body();
                    Log.i(SYNC_TAG, "ITANES_SYNC: " + remotePlaces.size() + " lugares recibidos");

                    if (!remotePlaces.isEmpty()) {
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
                                Log.e(SYNC_TAG, "ITANES_SYNC: Excepción al guardar en Room: " + e.getMessage(), e);
                                if (listener != null) {
                                    listener.onSyncFailure(e.getMessage());
                                }
                            }
                        });
                    } else {
                        Log.i(SYNC_TAG, "ITANES_SYNC: Lista vacía recibida de la API");
                        if (listener != null) {
                            listener.onSyncSuccess();
                        }
                    }
                } else {
                    String errorMsg = "Respuesta HTTP no exitosa. Código: " + response.code();
                    Log.e(SYNC_TAG, "ITANES_SYNC: " + errorMsg);
                    if (listener != null) {
                        listener.onSyncFailure(errorMsg);
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
