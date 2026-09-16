package com.example.itanes_la_libertad;

public class LocationValidator {

    public static boolean isValid(double latitude, double longitude) {
        return latitude >= -90.0 && latitude <= 90.0 && longitude >= -180.0 && longitude <= 180.0;
    }
}
