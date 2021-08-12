package com.lucidity;

import com.lucidity.input.FormattedFileInputReader;
import com.lucidity.input.InputReader;
import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import com.lucidity.solution.DijkstraPathFinder;
import com.lucidity.solution.HaversineDistanceCalculator;
import com.lucidity.solution.HuresticSelectionStrategy;
import com.lucidity.solution.PathFinder;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class SolutionEntryPoint {
    public static void main(String[] args) throws IOException {
        new SolutionEntryPoint().solve();
    }

    private void solve() throws IOException {
        InputReader inputReader = new FormattedFileInputReader();
        final Map<String, Destination> stringDestinationMap = inputReader.takeInput(getClass().getClassLoader().getResourceAsStream("input"));
        PathFinder pathFinder = new DijkstraPathFinder(new HaversineDistanceCalculator(), new HuresticSelectionStrategy());
        Destination hangerSavior = findHangerSavior(stringDestinationMap);
        System.out.println(pathFinder.solve(hangerSavior, stringDestinationMap).collect(Collectors.joining("->")));
    }

    private Destination findHangerSavior(final Map<String, Destination> stringDestinationMap) {
        return stringDestinationMap.values().stream().filter(destination -> destination.getDestinationType() == DestinationType.HUNGER_SAVIOR).findFirst().orElse(null);
    }
}
