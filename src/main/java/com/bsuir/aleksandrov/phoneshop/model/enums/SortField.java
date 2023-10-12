package com.bsuir.aleksandrov.phoneshop.model.enums;

import java.util.Arrays;

public enum SortField {
    brand,
    model,
    displaySizeInches,
    price;

    public static SortField getValue(String name) {
        return Arrays.stream(SortField.values())
                .filter(value -> value.name().equals(name))
                .findAny()
                .orElse(null);
    }
}
