package com.fran.jobsy.app.common;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public final class UriBuilder {

    private UriBuilder() {
    }

    public static URI buildCreatedLocation(Object id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
