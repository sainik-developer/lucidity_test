package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;
import com.lucidity.model.Triplet;
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
        final List<Destination> visitedRestaurant = new LinkedList<>();
        Destination nextSource = hungerSavior;
        double timeTakenInSec = 0.0;
        bestPath.add(nextSource.getNameIdentifier());
        Triplet<Destination, Double, List<String>> nextMoveByDijkstra;
        do {
            nextMoveByDijkstra = findBestNextMoveByDijkstra(nextSource, visitedRestaurant, destinationMap, timeTakenInSec, bestPath);
            if (nextMoveByDijkstra == null) {
                throw new IllegalArgumentException("No solution found!");
            }
            nextSource = nextMoveByDijkstra.getX();
            timeTakenInSec = nextMoveByDijkstra.getY();
            bestPath.addAll(nextMoveByDijkstra.getZ());
            bestPath.add(nextSource.getNameIdentifier());
            if (nextSource.getDestinationType() == DestinationType.RESTAURANT) {
                visitedRestaurant.add(nextSource);
            }
        } while (!bestPath.containsAll(destinationMap.keySet()));
        return bestPath.stream();
    }

    private Triplet<Destination, Double, List<String>> findBestNextMoveByDijkstra(Destination source, List<Destination> visitedRestaurant, final Map<String, Destination> destinationMap, double timeTakenInSec, final Set<String> alreadyVisitedDestinations) {
        Map<String, Quadruple<String, Double, String, DestinationType>> destinationCalculationMap = destinationMap.keySet().stream().collect(Collectors.toMap(s -> s, s -> new Quadruple<>(s, Double.MAX_VALUE, null, destinationMap.get(s).getDestinationType(), false)));
        destinationCalculationMap.get(source.getNameIdentifier()).setX(timeTakenInSec);
        Destination u = source;
        do {
            Destination finalU = u;
            u.getHasRoadToDestinations().forEach(destination -> {
                double temp = destinationCalculationMap.get(finalU.getNameIdentifier()).getX()
                        + Math.max(toTime(distanceCalculator.findDistance(finalU, destination)), destination.getAvailabilityTimeInSec() - timeTakenInSec);
                if (temp < destinationCalculationMap.get(destination.getNameIdentifier()).getX()) {
                    destinationCalculationMap.get(destination.getNameIdentifier()).setX(temp);
                    destinationCalculationMap.get(destination.getNameIdentifier()).setY(finalU.getNameIdentifier());
                }
            });
            destinationCalculationMap.get(u.getNameIdentifier()).setRemoved(true);
            Triplet<Destination, Double, List<String>> nextMoveByDijkstra = findNextDestination(source, destinationCalculationMap, destinationMap, visitedRestaurant, alreadyVisitedDestinations);
            if (nextMoveByDijkstra != null) {
                return nextMoveByDijkstra;
            }
            u = destinationMap.get(destinationCalculationMap.values().stream().filter(quadruple -> !quadruple.isRemoved()).min(Comparator.comparingDouble(Quadruple::getX)).map(Quadruple::getV).orElse(null));
        } while (destinationCalculationMap.values().stream().anyMatch(quadruple -> !quadruple.isRemoved()));
        return findNextDestination(source, destinationCalculationMap, destinationMap, visitedRestaurant, alreadyVisitedDestinations);
    }

    private Triplet<Destination, Double, List<String>> findNextDestination(Destination source, final Map<String, Quadruple<String, Double, String, DestinationType>> destinationCalculationMap,
                                                                           final Map<String, Destination> destinationMap,
                                                                           final List<Destination> visitedRestaurants,
                                                                           final Set<String> alreadyVisitedDestinations) {
        Destination nextDestination = selectionStrategy.nextSelectAsPerStrategy(source, destinationCalculationMap.values().stream(), destinationMap, visitedRestaurants, alreadyVisitedDestinations);
        return nextDestination != null ? getResult(source, nextDestination, destinationCalculationMap) : null;
    }

    private Triplet<Destination, Double, List<String>> getResult(final Destination source, final Destination nextDestination,
                                                                 final Map<String, Quadruple<String, Double, String, DestinationType>> destinationCalculationMap) {
        Quadruple<String, Double, String, DestinationType> quadrupleNext = destinationCalculationMap.values().stream()
                .filter(quadruple -> quadruple.getV().equals(nextDestination.getNameIdentifier()))
                .findFirst().orElse(null);
        double cost = quadrupleNext.getX();
        LinkedList<String> path = new LinkedList<>();
        do {
            path.add(quadrupleNext.getY());
            Quadruple<String, Double, String, DestinationType> finalQuadrupl111e = quadrupleNext;
            quadrupleNext = destinationCalculationMap.values().stream().filter(quadruple -> quadruple.getV().equals(finalQuadrupl111e.getY()))
                    .findFirst().orElse(null);
        } while (!quadrupleNext.getV().equals(source.getNameIdentifier()));
        LinkedList<String> reversedList = new LinkedList<>();
        path.descendingIterator().forEachRemaining(s -> reversedList.add(s));
        return new Triplet<>(nextDestination, cost, reversedList);
    }

    private double toTime(double distanceInKm) {
        return distanceInKm / 20 * 60 * 60;
    }
}
