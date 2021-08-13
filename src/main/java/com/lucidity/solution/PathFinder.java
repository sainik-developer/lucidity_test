package com.lucidity.solution;

import com.lucidity.model.Destination;

import java.util.Map;
import java.util.stream.Stream;

public interface PathFinder {
    Stream<String> solve(final Destination start, final Map<String, Destination> destinationMap);
}
