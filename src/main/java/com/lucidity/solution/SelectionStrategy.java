package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface SelectionStrategy {
    Destination nextSelectAsPerStrategy(final Destination source, final Stream<Quadruple<String, Double, String, DestinationType>> destinationCalculationMap,
                                        final Map<String, Destination> destinationMap,
                                        final List<Destination> visitedRestaurants,
                                        final Set<String> visitedDestinations);
}
