# productcatalogservice (ShopNest) — the project at the END of Class 15

This is the catalog service after Class 15 (Resilience). It starts from the project at the end of Class 14 (same as the end of Class 11; Classes 12 to 14 did not change the code). Spring Boot 4.1.1, Java 17 (also runs on 21), MySQL.

## What Class 15 changed

| File | Change |
|---|---|
| `pom.xml` | Added `spring-boot-starter-actuator`, `spring-boot-starter-aspectj`, `io.github.resilience4j:resilience4j-spring-boot4:2.4.0` |
| `application.properties` | Timeouts (`spring.http.clients.*`), `groq.url`, the `groq` circuit breaker settings, the actuator endpoint list |
| `ProductCatalogServiceApplication` | `@EnableResilientMethods` (turns on Spring's built-in `@Retryable`) |
| `clients/AuthClient` (new) | The one place that calls the auth service. `RestClient` + `@Retryable` (only HTTP 5xx, 3 retries, 200 ms, ×2, jitter 100 ms) |
| `clients/GroqClient` (new) | The one place that calls Groq. `RestClient` + `@CircuitBreaker(name = "groq")` |
| `security/TokenCheckFilter` | Uses `AuthClient`. When the auth service cannot be reached, it hands an `AuthServiceUnavailableException` to the exception handler |
| `services/PitchService` | Uses `GroqClient`. The old `try/catch` with the template sentence is the fallback |
| `exceptions/` (new) | `AuthServiceUnavailableException` and `ApiExceptionHandler` (HTTP 503, `application/problem+json`, `Retry-After: 5`) |
| `Friend.java` (new, not part of the app) | A pretend friend for practice. Run it with `java Friend.java` |

`DummyJsonProductService` is still on `RestTemplate` here. In class an agent migrates it (homework if there is no time). Both clients get the same timeouts from the two properties.

## Run it

1. MySQL 8.4 or newer, schema `productcatalogservice`, seed loaded (`seed/seed-products.sql`, after one start of the app).
2. Terminal 1, the pretend friend: `java Friend.java` (listens on port 9000).
3. Terminal 2, the shop (keep your own `DB_PASSWORD` and `GROQ_API_KEY`):

```bash
export AUTH_SERVICE_URL=http://localhost:9000
export GROQ_URL=http://localhost:9000/groq
mvn spring-boot:run
```

4. The friend's remote control, from any terminal:

```bash
curl "localhost:9000/set?delay=0&fail=0"            # healthy
curl "localhost:9000/set?delay=20000"               # slow: answers after 20 seconds
curl "localhost:9000/set?delay=0&fail=70"           # flaky: 70 percent of calls fail with 503
curl "localhost:9000/set?fail=100&code=401"         # always answers 401 (must NOT be retried)
```

5. Things to call:

```bash
curl -s localhost:8080/products/1/pitch
curl -s -i -X DELETE localhost:8080/products/999999 -H "Authorization: Bearer x"
curl -s localhost:8080/actuator/circuitbreakers
```

## What was tested (27 Oct 2026, JDK 21, Boot 4.1.1, Spring Framework 7.0.9, MySQL 8.4.11)

- 17 unit tests pass (`mvn test`).
- Healthy: a write passes the filter (404 for a missing id), the pitch comes from the friend.
- Slow friend, 10 writes at once, Tomcat limited to 10 workers: with no read timeout, a `GET /products` got no answer for 8 seconds (even the health check). With the 5 s read timeout, the writes ended in HTTP 503 at 5.0 s and the `GET` was answered after about 4 s. Same result on the old `RestTemplate` code and on the new `RestClient` code.
- Flaky friend (70 percent failures), 40 writes: 11 got through with no retry (old code), 31 with `@Retryable`. One failing request makes 4 calls to the friend and takes about 1.5 s.
- A 401 from the friend is not retried (the friend saw 1 call).
- Always-failing friend: the caller gets HTTP 503, `Content-Type: application/problem+json`, `Retry-After: 5`, and a clear message.
- Groq friend failing: after 5 failures the breaker is OPEN, and the friend saw 5 calls for 8 requests. After the 15 s wait, the next call moves the breaker to HALF_OPEN, and 3 good calls close it. While the breaker is OPEN, the pitch is the template sentence.

## Not tested

- Groq's real HTTP 429 (needs a real key and 30+ calls in one minute).
- JDK 17 at run time (compiled for 17, run on 21), Windows.
- The `DummyJsonProductService` migration in the app itself (the agent's version compiles; its calls were not run against dummyjson).
