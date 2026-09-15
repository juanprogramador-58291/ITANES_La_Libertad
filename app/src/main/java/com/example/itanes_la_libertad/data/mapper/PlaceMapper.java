package com.example.itanes_la_libertad.data.mapper;

import com.example.itanes_la_libertad.data.local.entity.PlaceEntity;
import com.example.itanes_la_libertad.data.remote.dto.PlaceRemoteDto;

import java.util.ArrayList;
import java.util.List;

public class PlaceMapper {

    public static PlaceEntity toEntity(PlaceRemoteDto dto) {
        if (dto == null) {
            return null;
        }
        return new PlaceEntity(
                dto.getId(),
                dto.getName(),
                dto.getShortDescription(),
                dto.getDescription(),
                dto.getAddress(),
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getImageUrl(),
                dto.getOrderNumber(),
                dto.getUpdatedAt()
        );
    }

    public static List<PlaceEntity> toEntityList(List<PlaceRemoteDto> remotePlaces) {
        if (remotePlaces == null) {
            return new ArrayList<>();
        }
        List<PlaceEntity> entityList = new ArrayList<>();
        for (PlaceRemoteDto dto : remotePlaces) {
            PlaceEntity entity = toEntity(dto);
            if (entity != null) {
                entityList.add(entity);
            }
        }
        return entityList;
    }
}
