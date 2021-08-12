package com.lucidity.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/***
 *
 */
@RequiredArgsConstructor
public enum DestinationType implements Comparable<DestinationType> {
    RESTAURANT("R"), CUSTOMER("C"), HUNGER_SAVIOR("H");
    private final String ID;

    public static DestinationType value(String s) {
        return Arrays.stream(values()).filter(destinationType -> destinationType.ID.equals(s)).findFirst().orElse(null);
    }

}
