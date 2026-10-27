package com.scaler.productcatalogservice.services;

import com.scaler.productcatalogservice.clients.GroqClient;
import com.scaler.productcatalogservice.dtos.GroqChatResponseDto;
import com.scaler.productcatalogservice.models.Product;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PitchService {

    private static final String MODEL = "openai/gpt-oss-20b";
    private static final String SYSTEM_PROMPT = "You are a marketing copywriter for ShopNest, an online store. "
            + "You write short push notifications that make people want to open the app.";

    private final GroqClient groqClient;

    public PitchService(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    public String generatePitch(Product product) {
        try {
            Map<String, Object> body = Map.of(
                    "model", MODEL,
                    "reasoning_effort", "low",
                    "max_completion_tokens", 300,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", buildPrompt(product))));

            GroqChatResponseDto response = groqClient.chat(body);

            String pitch = response.getChoices().get(0).getMessage().getContent();
            if (pitch == null || pitch.isBlank()) {
                throw new IllegalStateException("Groq sent an empty pitch");
            }
            return pitch.trim();
        } catch (Exception e) {
            log.warn("Groq call failed for product {}: {}", product.getId(), e.getMessage());
            return "New arrival: " + product.getName() + " for just $" + product.getPrice() + ". Check it out!";
        }
    }

    private String buildPrompt(Product product) {
        return """
                Write ONE push notification for this product.
                Rules:
                - Maximum 25 words.
                - Mention the price.
                - At most one emoji.
                - No hashtags.
                - Reply with only the notification text.

                Product: %s
                Category: %s
                Price: $%s
                Details: %s""".formatted(product.getName(), product.getCategory().getName(),
                product.getPrice(), product.getDescription());
    }
}
