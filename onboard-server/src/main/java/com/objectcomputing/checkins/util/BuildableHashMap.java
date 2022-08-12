package com.objectcomputing.checkins.util;

import java.util.HashMap;

/**
 * Created by Andrew Montgomery on 12/25/21.
 */
public class BuildableHashMap<T, T1> extends HashMap<T, T1> {

    public BuildableHashMap<T,T1> build(T key, T1 value) {
        this.put(key, value);
        return this;
    }
}
