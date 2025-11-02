package com.fran.jobsy.app.annotation;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Caching(evict = {
        @CacheEvict(value = "usersFull", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
        @CacheEvict(value = "usersPublic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()"),
        @CacheEvict(value = "usersBasic", key = "#root.target.authenticatedUserProvider.getAuthenticatedUserId()")
})
public @interface EvictAuthenticatedUserCaches {
}