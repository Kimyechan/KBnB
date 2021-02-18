package com.buildup.kbnb.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    USER("USER"),
    HOST("HOST");

    private String value;
}
