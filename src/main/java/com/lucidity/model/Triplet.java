package com.lucidity.model;

import lombok.Data;


@Data
public class Triplet<X,Y,Z> {
    private final X x;
    private final Y y;
    private final Z z;
}
