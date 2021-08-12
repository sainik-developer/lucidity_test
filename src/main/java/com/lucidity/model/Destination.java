package com.lucidity.model;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class Destination {
    private final Pair<Double, Double> latLong;
    private final DestinationType destinationType;
    private final List<Destination> hasRoadToDestinations;
    private final String nameIdentifier;
    private double t;
    private List<Destination> customers;

    public Destination(Pair<Double, Double> latLong, DestinationType destinationType, List<Destination> hasRoadToDestinations, String nameIdentifier, double t) {
        this.latLong = latLong;
        this.destinationType = destinationType;
        this.hasRoadToDestinations = hasRoadToDestinations;
        this.nameIdentifier = nameIdentifier;
        this.t = t;
    }
}
