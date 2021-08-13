package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * It's assumed that it's always better to visit the restaurant as that will give more freedom to reach destination at shortest time.
 */
public class HuresticSelectionStrategy implements SelectionStrategy {
    public Destination nextSelectAsPerStrategy(final Destination source, final Stream<Quadruple<String, Double, String, DestinationType>> destinationCalculationMap,
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
                            return destination == null;
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
}
