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
        double timeTaken = 0.0;
        bestPath.add(nextSource.getNameIdentifier());
        Triplet<Destination, Double, List<String>> nextMoveByDijkstra = new Triplet<>(nextSource, timeTaken, Collections.singletonList(nextSource.getNameIdentifier()));
        do {
            nextMoveByDijkstra = findBestNextMoveByDijkstra(nextSource, visitedRestaurant, destinationMap, timeTaken, bestPath);
            if (nextMoveByDijkstra == null) {
                throw new IllegalArgumentException("No solution found!");
            }
            nextSource = nextMoveByDijkstra.getX();
            timeTaken = nextMoveByDijkstra.getY();
            bestPath.addAll(nextMoveByDijkstra.getZ());
            bestPath.add(nextSource.getNameIdentifier());
            if (nextSource.getDestinationType() == DestinationType.RESTAURANT) {
                visitedRestaurant.add(nextSource);
            }
        } while (!bestPath.containsAll(destinationMap.keySet()));
        return bestPath.stream();
    }

    private Triplet<Destination, Double, List<String>> findBestNextMoveByDijkstra(Destination source, List<Destination> visitedRestaurant, final Map<String, Destination> destinationMap, double timeTaken, final Set<String> alreadyVisitedDestinations) {
        Map<String, Quadruple<String, Double, String, DestinationType>> destinationCalculationMap = destinationMap.keySet().stream().collect(Collectors.toMap(s -> s, s -> new Quadruple<>(s, Double.MAX_VALUE, null, destinationMap.get(s).getDestinationType(), false)));
        destinationCalculationMap.get(source.getNameIdentifier()).setX(timeTaken);
        Destination u = source;
        do {
            Destination finalU = u;
            u.getHasRoadToDestinations().forEach(destination -> {
                double temp = destinationCalculationMap.get(finalU.getNameIdentifier()).getX()
                        + Math.max(toTime(distanceCalculator.findDistance(finalU, destination)), destination.getT() - timeTaken);
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
        Destination nextDestination = nextSelectTretegy(source, destinationCalculationMap.values().stream(), destinationMap, visitedRestaurants, alreadyVisitedDestinations);
        return nextDestination != null ? getResult(source, nextDestination, destinationCalculationMap) : null;
    }

    private Destination nextSelectTretegy(final Destination source, final Stream<Quadruple<String, Double, String, DestinationType>> destinationCalculationMap,
                                          final Map<String, Destination> destinationMap,
                                          final List<Destination> visitedRestaurants,
                                          final Set<String> visitedDestinations) {
        Map<String, Destination> visitedRestaurantMap = visitedRestaurants.stream().collect(Collectors.toMap(Destination::getNameIdentifier, destination -> destination));
        return destinationCalculationMap
                .filter(quadruple -> !quadruple.getV().equals(source.getNameIdentifier()))
                .sorted((o1, o2) -> {
                    if (Double.compare(o1.getX(), o2.getX()) == 0) {
                        return o1.getZ() == DestinationType.RESTAURANT &&
                                o2.getZ() == DestinationType.RESTAURANT ? 0 :
                                o1.getZ() == DestinationType.RESTAURANT && o2.getZ() != DestinationType.RESTAURANT ?
                                        -1 : 1;
                    } else {
                        return Double.compare(o1.getX(), o2.getX());
                    }
                })
                .filter(quadruple -> {
                    if (!visitedDestinations.contains(quadruple.getV())) {
                        if (quadruple.getZ() == DestinationType.RESTAURANT) {
                            Destination destination = visitedRestaurantMap.get(quadruple.getV());
                            if (destination == null) {
                                return true;
                            }
                        } else if (quadruple.getZ() == DestinationType.CUSTOMER) {
                            return isCustomerOfVisitedRestaurant(visitedRestaurants, quadruple.getV());
                        }
                    }
                    return false;
                })
                .findFirst()
                .map(stringDoubleStringDestinationTypeQuadruple -> destinationMap.get(stringDoubleStringDestinationTypeQuadruple.getV()))
                .orElse(null);
    }

    private boolean isCustomerOfVisitedRestaurant(List<Destination> destinations, String name) {
        return destinations.stream()
                .flatMap(destination -> destination.getCustomers().stream())
                .anyMatch(destination -> destination.getNameIdentifier().equals(name));
    }

    private Triplet<Destination, Double, List<String>> getResult(final Destination source, final Destination nextDestination,
                                                                 final Map<String, Quadruple<String, Double, String, DestinationType>> destinationCalculationMap) {
        Quadruple<String, Double, String, DestinationType> quadrupl111e = destinationCalculationMap.values().stream()
                .filter(quadruple -> quadruple.getV().equals(nextDestination.getNameIdentifier()))
                .findFirst().orElse(null);
        double cost = quadrupl111e.getX();
        LinkedList<String> path = new LinkedList<>();
        do {
            path.add(quadrupl111e.getY());
            Quadruple<String, Double, String, DestinationType> finalQuadrupl111e = quadrupl111e;
            quadrupl111e = destinationCalculationMap.values().stream().filter(quadruple -> quadruple.getV().equals(finalQuadrupl111e.getY()))
                    .findFirst().orElse(null);
        } while (!quadrupl111e.getV().equals(source.getNameIdentifier()));
        LinkedList<String> sjns = new LinkedList<>();
        path.descendingIterator().forEachRemaining(s -> sjns.add(s));
        return new Triplet<>(nextDestination, cost, sjns);

    }

    private double toTime(double distanceInKm) {
        return distanceInKm / 20 * 60 * 60;
    }
}
