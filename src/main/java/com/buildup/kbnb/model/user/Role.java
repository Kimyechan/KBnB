package com.buildup.kbnb.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    USER("ROLE_USER"),
    HOST("ROLE_HOST");

    private String value;
}
