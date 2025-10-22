package com.fran.jobsy.app.config.websockets;

import java.security.Principal;

public record StompPrincipal(String name) implements Principal {
    @Override
    public String getName() {
        return name;
    }
}
