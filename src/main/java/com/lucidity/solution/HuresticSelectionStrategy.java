package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HuresticSelectionStrategy implements SelectionStrategy {

    @Override
    public Destination findNextNode(Stream<Quadruple<String, Double, String, DestinationType>> quadruple, Map<String, Destination> destinationMap, List<Destination> visitedRes) {
        Map<String, Destination> visitedRestaurant = visitedRes.stream().collect(Collectors.toMap(Destination::getNameIdentifier, destination -> destination));
        return quadruple
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
                .filter(stringDoubleStringDestinationTypeQuadruple -> {
                    if (stringDoubleStringDestinationTypeQuadruple.getZ() == DestinationType.RESTAURANT) {
                        Destination destination = visitedRestaurant.get(stringDoubleStringDestinationTypeQuadruple.getV());
                        if (destination == null) {
                            return true;
                        }
                    } else if (stringDoubleStringDestinationTypeQuadruple.getZ() == DestinationType.CUSTOMER) {
                        return isCustomerOfVisitedRestaurant(visitedRes, stringDoubleStringDestinationTypeQuadruple.getV());
                    } else if (stringDoubleStringDestinationTypeQuadruple.getZ() == DestinationType.HUNGER_SAVIOR) {
                        return true;
                    }
                    return false;
                })
                .findAny()
                .map(stringDoubleStringDestinationTypeQuadruple -> destinationMap.get(stringDoubleStringDestinationTypeQuadruple.getV()))
                .orElse(null);
    }

    private boolean isCustomerOfVisitedRestaurant(List<Destination> destinations, String name) {
        return destinations.stream().flatMap(destination -> destination.getHasRoadToDestinations().stream()).anyMatch(destination -> destination.getNameIdentifier().equals(name));
    }

}
