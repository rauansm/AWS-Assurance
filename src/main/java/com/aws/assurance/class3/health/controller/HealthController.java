package com.aws.assurance.class3.health.controller;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private static final double PI_RAD = Math.PI / 180.0;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> checkHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/calculate-distance")
    public ResponseEntity<Map<String, Double>> calculateDistance(@RequestParam final String latOrigin, @RequestParam final String longOrigin,
            @RequestParam final String latDest, @RequestParam final String longDest) {

        if (!isValidLatitude(latOrigin) || !isValidLatitude(latDest) || !isValidLongitude(longOrigin) || !isValidLongitude(longDest)) {
            throw new IllegalArgumentException("invalid_coordinates");
        }

        final double latitude1 = Double.parseDouble(latOrigin);
        final double longitude1 = Double.parseDouble(longOrigin);
        final double latitude2 = Double.parseDouble(latDest);
        final double longitude2 = Double.parseDouble(longDest);

        final double distanceKm = greatCircleInKilometers(
                latitude1,
                longitude1,
                latitude2,
                longitude2);

        final double distanceMeters = greatCircleInMeters(distanceKm);

        final double roundedKm = Math.round(distanceKm * 100.0) / 100.0;
        final double roundedMeters = Math.round(distanceMeters * 100.0) / 100.0;

        final Map<String, Double> response = new HashMap<>();
        response.put("distanceKm", roundedKm);
        response.put("distanceMeters", roundedMeters);

        return ResponseEntity.ok(response);

    }

    private static double greatCircleInMeters(final double distanceKm) {
        return distanceKm * 1000;
    }

    private static double greatCircleInKilometers(final double lat1, final double long1, final double lat2, final double long2) {
        final double phi1 = lat1 * PI_RAD;
        final double phi2 = lat2 * PI_RAD;
        final double lam1 = long1 * PI_RAD;
        final double lam2 = long2 * PI_RAD;

        return 6371.01 * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1));
    }

    private static boolean isValidLatitude(final String latitude) {
        return NumberUtils.isCreatable(latitude) && latitudeBetweenValidInterval(latitude);
    }

    private static boolean latitudeBetweenValidInterval(final String latitude) {
        return NumberUtils.toDouble(latitude) >= -90d && NumberUtils.toDouble(latitude) <= 90d;
    }

    private static boolean isValidLongitude(final String longitude) {
        return NumberUtils.isCreatable(longitude) && longitudeBetweenValidInterval(longitude);
    }

    private static boolean longitudeBetweenValidInterval(final String longitude) {
        return NumberUtils.toDouble(longitude) >= -180d && NumberUtils.toDouble(longitude) <= 180d;
    }
}