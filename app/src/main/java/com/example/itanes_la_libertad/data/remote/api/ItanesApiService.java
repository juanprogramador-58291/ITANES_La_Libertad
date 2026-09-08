package com.example.itanes_la_libertad.data.remote.api;

import com.example.itanes_la_libertad.data.remote.dto.PlaceRemoteDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ItanesApiService {

    @GET("API/places.json")
    Call<List<PlaceRemoteDto>> getPlaces();
}
