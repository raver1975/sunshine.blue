package com.klemstinegroup.sunshineblue.engine.util;

/**
 *
 * @author Raymond
 */
public class Tuple<X, Y> {

    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x.toString();
    }
}
