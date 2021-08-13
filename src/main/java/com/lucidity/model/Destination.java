package com.lucidity.model;

import javafx.util.Pair;
import lombok.Data;

import java.util.List;


@Data
public class Destination {
    private final Pair<Double, Double> latLong;
    private final DestinationType destinationType;
    private final List<Destination> hasRoadToDestinations;
    private final String nameIdentifier;
    private final double t;
    private List<Destination> customers;
}
