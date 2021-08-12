package com.lucidity.solution;

import com.lucidity.model.Destination;

public class HaversineDistanceCalculator implements DistanceCalculator {
    @Override
    public double findDistance(Destination start, Destination end) {

        double dLat = Math.toRadians(end.getLatLong().getKey() - start.getLatLong().getKey());
        double dLon = Math.toRadians(end.getLatLong().getValue() - start.getLatLong().getValue());

        // convert to radians
        double lat1 = Math.toRadians(start.getLatLong().getKey());
        double lat2 = Math.toRadians(end.getLatLong().getKey());

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
}
