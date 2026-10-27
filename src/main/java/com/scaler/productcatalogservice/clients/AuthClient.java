package com.scaler.productcatalogservice.clients;

import com.scaler.productcatalogservice.dtos.AuthUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

// The one place that talks to the auth service
@Component
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(RestClient.Builder builder, @Value("${auth.service.url}") String authServiceUrl) {
        this.restClient = builder.baseUrl(authServiceUrl).build();
    }

    // Retry only when the other side says "I am having a bad moment" (HTTP 5xx).
    // Wait 200 ms, then 400, then 800, each time a little random.
    @Retryable(includes = HttpServerErrorException.class,
            maxRetries = 3, delay = 200, multiplier = 2, jitter = 100)
    public AuthUserDto validateToken(String authorizationHeader) {
        return restClient.get()
                .uri("/auth/validateToken")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(AuthUserDto.class);
    }
}
