package br.com.agendafacil.api.domain;

import lombok.Getter;

@Getter
public enum UserRole {

    ROLE_CLIENT("Cliente"),
    ROLE_EMPRESARIO("Empresario"),
    ROLE_ADMIN("Admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
}
