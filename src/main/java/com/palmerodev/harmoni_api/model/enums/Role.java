package com.palmerodev.harmoni_api.model.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum Role {
    GUEST(List.of(Permission.GUEST)),
    USER(List.of(Permission.CUSTOMER, Permission.GUEST)),
    ADMIN(List.of(Permission.ROOT, Permission.CUSTOMER, Permission.GUEST)),
    ;

    private final List<Permission> permissions;

    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
