package com.bsuir.aleksandrov.phoneshop.model.enums;

import java.util.Arrays;

public enum SortOrder {
    asc,
    desc;

    public static SortOrder getValue(String name) {
        return Arrays.stream(SortOrder.values())
                .filter(value -> value.name().equals(name))
                .findAny()
                .orElse(null);
    }
}
