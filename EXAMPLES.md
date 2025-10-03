# Gateway Route Configuration Examples

This document provides practical examples for configuring routes in your Spring Cloud Gateway.

## Example 1: Simple Path-Based Routing

Route requests to a backend service based on path:

```yaml
# In application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
```

**Or via Web UI:**
- **Route ID**: `user-service`
- **URI**: `http://localhost:8081`
- **Predicate**: Name=`Path`, Args=`_genkey_0=/api/users/**`
- **Filter**: Name=`StripPrefix`, Args=`_genkey_0=1`

## Example 2: Load-Balanced Service

Route to a load-balanced microservice:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
```

**Via Web UI:**
- **Route ID**: `order-service`
- **URI**: `lb://order-service`
- **Predicate**: Name=`Path`, Args=`_genkey_0=/api/orders/**`

## Example 3: Method-Based Routing

Route only specific HTTP methods:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: admin-api
          uri: http://localhost:8082
          predicates:
            - Path=/admin/**
            - Method=GET,POST
```

**Via Web UI:**
- **Route ID**: `admin-api`
- **URI**: `http://localhost:8082`
- **Predicates**:
  - Predicate 1: Name=`Path`, Args=`_genkey_0=/admin/**`
  - Predicate 2: Name=`Method`, Args=`_genkey_0=GET`, `_genkey_1=POST`

## Example 4: Header-Based Routing

Route based on request headers:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: api-v2
          uri: http://localhost:8083
          predicates:
            - Path=/api/**
            - Header=X-API-Version, v2
```

**Via Web UI:**
- **Route ID**: `api-v2`
- **URI**: `http://localhost:8083`
- **Predicates**:
  - Predicate 1: Name=`Path`, Args=`_genkey_0=/api/**`
  - Predicate 2: Name=`Header`, Args=`_genkey_0=X-API-Version`, `_genkey_1=v2`

## Example 5: Adding Request Headers

Add custom headers to forwarded requests:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: backend-service
          uri: http://localhost:8084
          predicates:
            - Path=/backend/**
          filters:
            - StripPrefix=1
            - AddRequestHeader=X-Gateway-Request, true
```

**Via Web UI:**
- **Route ID**: `backend-service`
- **URI**: `http://localhost:8084`
- **Predicate**: Name=`Path`, Args=`_genkey_0=/backend/**`
- **Filters**:
  - Filter 1: Name=`StripPrefix`, Args=`_genkey_0=1`
  - Filter 2: Name=`AddRequestHeader`, Args=`_genkey_0=X-Gateway-Request`, `_genkey_1=true`

## Example 6: Path Rewriting

Rewrite the request path before forwarding:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: legacy-api
          uri: http://localhost:8085
          predicates:
            - Path=/v1/api/**
          filters:
            - RewritePath=/v1/api/(?<segment>.*), /api/${segment}
```

**Via Web UI:**
- **Route ID**: `legacy-api`
- **URI**: `http://localhost:8085`
- **Predicate**: Name=`Path`, Args=`_genkey_0=/v1/api/**`
- **Filter**: Name=`RewritePath`, Args=`_genkey_0=/v1/api/(?<segment>.*)`, `_genkey_1=/api/${segment}`

## Example 7: Circuit Breaker

Add resilience with circuit breaker:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: resilient-service
          uri: http://localhost:8086
          predicates:
            - Path=/api/external/**
          filters:
            - name: CircuitBreaker
              args:
                name: myCircuitBreaker
                fallbackUri: forward:/fallback
```

**Via Web UI:**
- **Route ID**: `resilient-service`
- **URI**: `http://localhost:8086`
- **Predicate**: Name=`Path`, Args=`_genkey_0=/api/external/**`
- **Filter**: Name=`CircuitBreaker`, Args=`name=myCircuitBreaker`, `fallbackUri=forward:/fallback`

## Example 8: Rate Limiting

Limit request rate per user:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rate-limited-api
          uri: http://localhost:8087
          predicates:
            - Path=/api/public/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

## Example 9: Multiple Predicates and Filters

Complex routing with multiple conditions:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: complex-route
          uri: lb://payment-service
          predicates:
            - Path=/api/payments/**
            - Method=POST,PUT
            - Header=Authorization, Bearer .*
          filters:
            - StripPrefix=2
            - AddRequestHeader=X-Request-Source, gateway
            - AddResponseHeader=X-Response-Time, ${T(System).currentTimeMillis()}
          order: 1
```

**Via Web UI:**
- **Route ID**: `complex-route`
- **URI**: `lb://payment-service`
- **Order**: `1`
- **Predicates**:
  - Predicate 1: Name=`Path`, Args=`_genkey_0=/api/payments/**`
  - Predicate 2: Name=`Method`, Args=`_genkey_0=POST`, `_genkey_1=PUT`
  - Predicate 3: Name=`Header`, Args=`_genkey_0=Authorization`, `_genkey_1=Bearer .*`
- **Filters**:
  - Filter 1: Name=`StripPrefix`, Args=`_genkey_0=2`
  - Filter 2: Name=`AddRequestHeader`, Args=`_genkey_0=X-Request-Source`, `_genkey_1=gateway`
  - Filter 3: Name=`AddResponseHeader`, Args=`_genkey_0=X-Response-Time`, `_genkey_1=${T(System).currentTimeMillis()}`

## Example 10: Host-Based Routing

Route based on the Host header:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: subdomain-route
          uri: http://localhost:8088
          predicates:
            - Host=api.example.com
            - Path=/api/**
```

**Via Web UI:**
- **Route ID**: `subdomain-route`
- **URI**: `http://localhost:8088`
- **Predicates**:
  - Predicate 1: Name=`Host`, Args=`_genkey_0=api.example.com`
  - Predicate 2: Name=`Path`, Args=`_genkey_0=/api/**`

## Testing Your Routes

### Using cURL

```bash
# Test a simple route
curl http://localhost:8087/api/users

# Test with specific method
curl -X POST http://localhost:8087/api/orders

# Test with headers
curl -H "X-API-Version: v2" http://localhost:8087/api/data

# Test with authentication
curl -H "Authorization: Bearer token123" http://localhost:8087/api/payments
```

### Using the Web UI

1. Navigate to `http://localhost:8087/admin/routes`
2. Click "Create New Route"
3. Fill in the form with your configuration
4. Click "Create Route"
5. Test your route immediately

### Viewing Active Routes

```bash
# Via Actuator endpoint
curl http://localhost:8087/actuator/gateway/routes

# Via REST API
curl http://localhost:8087/api/routes

# Via Web UI
# Navigate to http://localhost:8087/admin/routes
```

## Tips for Web UI Configuration

### Predicate Arguments Format

When entering predicate arguments in the web UI, use this format (one per line):
```
_genkey_0=/api/users/**
_genkey_1=value2
```

### Filter Arguments Format

For filters, use the same format:
```
_genkey_0=1
name=myCircuitBreaker
fallbackUri=forward:/fallback
```

### Common Argument Keys

- **Path predicate**: `_genkey_0=/path/**`
- **Method predicate**: `_genkey_0=GET`, `_genkey_1=POST`
- **Header predicate**: `_genkey_0=HeaderName`, `_genkey_1=HeaderValue`
- **StripPrefix filter**: `_genkey_0=1` (number of segments to strip)
- **AddRequestHeader filter**: `_genkey_0=HeaderName`, `_genkey_1=HeaderValue`

## Troubleshooting

### Route not matching
- Check the order of routes (lower numbers have higher priority)
- Verify predicates are correctly configured
- Check logs for route matching details

### 404 Not Found
- Ensure the target URI is correct and accessible
- Verify the path predicate matches your request
- Check if StripPrefix is configured correctly

### Headers not being added
- Verify filter configuration
- Check filter order (some filters must come before others)
- Review actuator endpoint for active configuration

## Additional Resources

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Gateway Predicates Reference](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gateway-request-predicates-factories)
- [Gateway Filters Reference](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gatewayfilter-factories)
