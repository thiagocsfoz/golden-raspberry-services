package com.outsera.goldenraspberryservices.config;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@Priority(1000)
public class ApiKeyAuthFilter implements ContainerRequestFilter {

    @Inject
    @ConfigProperty(name = "api.secret.key")
    String secretKey;

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String providedKey = requestContext.getHeaderString(API_KEY_HEADER);

        if (providedKey == null || !providedKey.equals(secretKey)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Unauthorized access. Invalid or missing API key.\"}")
                            .type("application/json")
                            .build()
            );
        }
    }
}