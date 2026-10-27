package com.scaler.productcatalogservice.clients;

import com.scaler.productcatalogservice.dtos.GroqChatResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// The one place that talks to Groq
@Component
public class GroqClient {

    private final RestClient restClient;

    public GroqClient(RestClient.Builder builder,
                      @Value("${groq.url}") String url,
                      @Value("${groq.api-key}") String apiKey) {
        this.restClient = builder.baseUrl(url)
                .defaultHeaders(headers -> headers.setBearerAuth(apiKey))
                .build();
    }

    // After 5 calls with half or more failing, this stops calling Groq for 15 seconds
    @CircuitBreaker(name = "groq")
    public GroqChatResponseDto chat(Map<String, Object> body) {
        return restClient.post()
                .body(body)
                .retrieve()
                .body(GroqChatResponseDto.class);
    }
}
