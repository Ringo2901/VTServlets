package com.bsuir.aleksandrov.phoneshop.model.entities.user;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;

public enum UserRole {
    Admin,
    User,
    Visitor;

    @Override
    public String toString() {
        switch (this) {
            case Admin:
                return "Admin";
            case User:
                return "User";
            case Visitor:
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
