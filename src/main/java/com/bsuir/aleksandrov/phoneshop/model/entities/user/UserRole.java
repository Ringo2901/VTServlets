package com.bsuir.aleksandrov.phoneshop.model.entities.user;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;

public enum UserRole {
    admin,
    user,
    visitor;

    @Override
    public String toString() {
        switch (this) {
            case admin:
                return "Admin";
            case user:
                return "User";
            case visitor:
                return "Visitor";
            default:
                return "UNKNOWN";
        }
    }
    public static UserRole fromString(String status) {
        for (UserRole userRole : values()) {
            if (userRole.toString().equalsIgnoreCase(status)) {
                return userRole;
            }
        }
        return null;
    }
}
