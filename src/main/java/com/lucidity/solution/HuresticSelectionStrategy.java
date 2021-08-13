package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * It's assumed that it's always better to visit the restaurant as that will give more freedom to reach destination as shortest time.
 */
public class HuresticSelectionStrategy implements SelectionStrategy {
    @Override
    public Destination findNextNode(Stream<Quadruple<String, Double, String, DestinationType>> destinationCalculationMap, Map<String, Destination> destinationMap, List<Destination> visitedRestaurants) {
        Map<String, Destination> visitedRestaurantMap = visitedRestaurants.stream().collect(Collectors.toMap(Destination::getNameIdentifier, destination -> destination));
        return destinationCalculationMap
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
                    if (quadruple.getZ() == DestinationType.RESTAURANT) {
                        Destination destination = visitedRestaurantMap.get(quadruple.getV());
                        if (destination == null) {
                            return true;
                        }
                    } else if (quadruple.getZ() == DestinationType.CUSTOMER) {
                        return isCustomerOfVisitedRestaurant(visitedRestaurants, quadruple.getV());
                    } else if (quadruple.getZ() == DestinationType.HUNGER_SAVIOR) {
                        return true;
                    }
                    return false;
                })
                .findFirst()
                .map(stringDoubleStringDestinationTypeQuadruple -> destinationMap.get(stringDoubleStringDestinationTypeQuadruple.getV()))
                .orElse(null);
    }

    private boolean isCustomerOfVisitedRestaurant(List<Destination> destinations, String name) {
        return destinations.stream().flatMap(destination -> destination.getHasRoadToDestinations().stream()).anyMatch(destination -> destination.getNameIdentifier().equals(name));
    }
}
