package com.lucidity.solution;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.model.Quadruple;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface SelectionStrategy {
    Destination findNextNode(Stream<Quadruple<String, Double, String, DestinationType>> quadruple, final Map<String, Destination> destinationMap, List<Destination> visitedRes);
}
