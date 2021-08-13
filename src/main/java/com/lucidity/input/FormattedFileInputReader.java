package com.lucidity.input;

import com.lucidity.model.Destination;
import com.lucidity.model.DestinationType;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class FormattedFileInputReader implements InputReader {
    @Override
    public Map<String, Destination> takeInput(final InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, Destination> nameDestinationMap = Arrays.stream(bufferedReader.readLine().split(";"))
                .map(this::create)
                .collect(Collectors.toMap(Destination::getNameIdentifier, destination -> destination));
        readPath(nameDestinationMap, bufferedReader.readLine());
        readCustomerRestaurantRelations(nameDestinationMap, bufferedReader.readLine());
        return nameDestinationMap;
    }

    private Destination create(final String str) {
        final String name = str.split("=")[0];
        final Double lat = Double.parseDouble(str.split("=")[1].split(",")[0]);
        final Double longitude = Double.parseDouble(str.split("=")[1].split(",")[1]);
        final double time = Double.parseDouble(str.split("=")[1].split(",")[2]);
        final DestinationType destinationType = DestinationType.value(str.split("=")[1].split(",")[3]);
        return new Destination(new Pair<>(lat, longitude), destinationType, new LinkedList<>(), name, time);
    }

    private void readPath(final Map<String, Destination> destinationMap, final String s) {
        Arrays.stream(s.split(";")).forEach(s1 -> {
            Destination start = destinationMap.get(s1.split("-")[0]);
            Destination end = destinationMap.get(s1.split("-")[1]);
            start.getHasRoadToDestinations().add(end);
            end.getHasRoadToDestinations().add(start);
        });
    }

    private void readCustomerRestaurantRelations(final Map<String, Destination> destinationMap, final String s) {
        Arrays.stream(s.split(";"))
                .forEach(s1 -> destinationMap.get(s1.split("=")[0]).setCustomers(Arrays.stream(s1.split("=")[1].split(",")).map(destinationMap::get).collect(Collectors.toList())));
    }
}
