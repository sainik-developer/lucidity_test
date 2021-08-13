package com.lucidity.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Quadruple<V, X, Y, Z> {
    private V v;
    private X x;
    private Y y;
    private Z z;
    private boolean removed;
}
