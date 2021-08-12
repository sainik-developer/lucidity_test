package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * POSTULATION:
 */
@RequiredArgsConstructor
public class DijkstraPathFinder implements PathFinder {

    private final DistanceCalculator distanceCalculator;
    private final SelectionStrategy selectionStrategy;

    @Override
    public Stream<String> solve(final Destination hungerSavior, final Map<String, Destination> destinationMap) {
        final Set<String> bestPath = new LinkedHashSet<>();
        bestPath.add(hungerSavior.getNameIdentifier());
        final List<Destination> visitedRestaurant = new LinkedList<>();
        Destination nextSource = hungerSavior;
        double timeTaken = 0.0;
        while (!bestPath.containsAll(destinationMap.keySet())) {
            Pair<Destination, Double> nextMoveByDijkstra = findBestNextMoveByDijkstra(nextSource, visitedRestaurant, destinationMap, timeTaken);
            nextSource = nextMoveByDijkstra.getKey();
            timeTaken = nextMoveByDijkstra.getValue();
            if (nextSource != null) {
                bestPath.add(nextSource.getNameIdentifier());
                if (nextSource.getDestinationType() == DestinationType.RESTAURANT) {
                    visitedRestaurant.add(nextSource);
                }
            } else {
                throw new IllegalArgumentException("No solution found!");
            }
        }
        return bestPath.stream();
    }

    private Pair<Destination, Double> findBestNextMoveByDijkstra(Destination source, List<Destination> visitedRestaurant, final Map<String, Destination> destinationMap, double timeTaken) {
        Map<String, Quadruple<String, Double, String, DestinationType>> destinationCalculationMap = destinationMap.keySet().stream().collect(Collectors.toMap(s -> s, s -> new Quadruple<>(s, Double.MAX_VALUE, null, destinationMap.get(s).getDestinationType())));
        destinationCalculationMap.get(source.getNameIdentifier()).setX(0.0);
        while (!destinationCalculationMap.isEmpty()) {
            Destination u = selectionStrategy.findNextNode(destinationCalculationMap.values().stream(), destinationMap, visitedRestaurant);
            u.getHasRoadToDestinations().forEach(destination -> {
                double temp = destinationCalculationMap.get(u.getNameIdentifier()).getX()
                        + Math.max(toTime(distanceCalculator.findDistance(u, destination)), destination.getT() - timeTaken);
                if (temp < destinationCalculationMap.get(destination.getNameIdentifier()).getX()) {
                    destinationCalculationMap.get(destination.getNameIdentifier()).setX(temp);
                    destinationCalculationMap.get(destination.getNameIdentifier()).setY(u.getNameIdentifier());
                }
            });
            destinationCalculationMap.remove(u.getNameIdentifier());
            Destination foundNextDestination = selectionStrategy.findNextNode(destinationCalculationMap.values().stream(), destinationMap, visitedRestaurant);
            if (foundNextDestination != null) {
                return new Pair<>(foundNextDestination, destinationCalculationMap.get(foundNextDestination.getNameIdentifier()).getX());
            }
        }
        return null;
    }

    private double toTime(double distanceInKm) {
        return distanceInKm / 20 * 60 * 60;
    }
}
