package com.fran.jobsy.app.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public final class RestUtils {

    private RestUtils() {
    }

    public static URI buildCreatedLocation(Object id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
