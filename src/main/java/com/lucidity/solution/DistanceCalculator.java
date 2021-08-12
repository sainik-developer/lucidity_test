package com.lucidity.solution;

import com.lucidity.model.Destination;

/***
 *
 */
public interface DistanceCalculator {
    double findDistance(final Destination start, final Destination end);
}
