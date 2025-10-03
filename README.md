# Spring Cloud Gateway with Dynamic Configuration

A Spring Cloud Gateway project that supports both **YAML-based configuration** (traditional) and **web-based dynamic configuration** using Thymeleaf.

## Features

✅ **Dual Configuration Methods**:
- Traditional YAML configuration in `application.yml`
- Dynamic web-based configuration through Thymeleaf UI

✅ **Dynamic Route Management**:
- Create, update, delete, and view routes at runtime
- No restart required for route changes

✅ **Modern Web Interface**:
- Beautiful, responsive UI built with Thymeleaf
- Easy-to-use forms for route configuration
- Real-time route management

✅ **REST API**:
- Full REST API for programmatic route management
- Endpoints for CRUD operations on routes

## Technology Stack

- **Spring Boot 3.5.6**
- **Spring Cloud Gateway 2024.0.0**
- **Spring Cloud LoadBalancer**
- **Thymeleaf** (for web UI)
- **Spring Web MVC** (for Thymeleaf controllers)
- **Lombok** (for cleaner code)
- **Maven** (build tool)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`
You can set the admin username and password in `application.yml`:

```yaml
spring:
  security:
    user:
      name: admin
      password: admin
```

## Usage

### Web Interface

Access the web-based route management interface at:
```
http://localhost:8080/admin/routes
```

From here you can:
- **View all routes**: See all configured routes with their details
- **Create new routes**: Add routes dynamically through a form
- **Edit routes**: Modify existing route configurations
- **Delete routes**: Remove routes that are no longer needed
- **View route details**: See complete information about any route

### YAML Configuration

You can also configure routes in `application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: example-route
          uri: https://httpbin.org
          predicates:
            - Path=/httpbin/**
          filters:
            - StripPrefix=1
```

### REST API

The application provides REST endpoints for programmatic access:

#### Get All Routes
```bash
GET http://localhost:8080/api/routes
```

#### Get Route by ID
```bash
GET http://localhost:8080/api/routes/{routeId}
```

#### Create New Route
```bash
POST http://localhost:8080/api/routes
Content-Type: application/json

{
  "id": "my-service-route",
  "uri": "http://localhost:8081",
  "predicates": [
    {
      "name": "Path",
      "args": {
        "_genkey_0": "/api/myservice/**"
      }
    }
  ],
  "filters": [
    {
      "name": "StripPrefix",
      "args": {
        "_genkey_0": "2"
      }
    }
  ],
  "order": 0,
  "description": "Route to my service"
}
```

#### Update Route
```bash
PUT http://localhost:8080/api/routes/{routeId}
Content-Type: application/json

{
  "id": "my-service-route",
  "uri": "http://localhost:8082",
  ...
}
```

#### Delete Route
```bash
DELETE http://localhost:8080/api/routes/{routeId}
```

## Common Predicates

- **Path**: Match by request path
  - Example: `Path=/api/users/**`
- **Method**: Match by HTTP method
  - Example: `Method=GET,POST`
- **Header**: Match by request header
  - Example: `Header=X-Request-Id, \d+`
- **Query**: Match by query parameter
  - Example: `Query=foo, ba.`
- **Host**: Match by host header
  - Example: `Host=**.example.com`

## Common Filters

- **StripPrefix**: Remove path segments
  - Example: `StripPrefix=1` (removes first segment)
- **AddRequestHeader**: Add header to request
  - Example: `AddRequestHeader=X-Request-Foo, Bar`
- **AddResponseHeader**: Add header to response
  - Example: `AddResponseHeader=X-Response-Foo, Bar`
- **RewritePath**: Rewrite request path
  - Example: `RewritePath=/api/(?<segment>.*), /${segment}`
- **CircuitBreaker**: Add circuit breaker
  - Example: `CircuitBreaker=myCircuitBreaker`

## Configuration

### Application Properties

Key configuration in `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      routes:
        # Static routes configured here
  thymeleaf:
    cache: false

management:
  endpoints:
    web:
      exposure:
        include: gateway,health,info
```

## Project Structure

```
gateway/
├── .git/                     # Git version control
├── .mvn/                     # Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/com/doors/gateway/
│   │   │   ├── config/          # Configuration classes (GatewayConfig, SecurityConfig, etc.)
│   │   │   ├── controller/      # Web & REST controllers (LoginController, GatewayWebController, etc.)
│   │   │   ├── model/           # Domain models (RouteDefinition, PredicateDefinition, etc.)
│   │   │   ├── service/         # Services (DynamicRouteService)
│   │   │   ├── util/            # Utility classes (MapConverter, etc.)
│   │   │   └── GatewayApplication.java  # Main Spring Boot application
│   │   └── resources/
│   │       ├── static/          # Static assets
│   │       │   ├── css/         # Stylesheets (login.css, routes.css, etc.)
│   │       │   └── js/          # JavaScript (routes-form.js, advanced-form.js)
│   │       ├── templates/       # Thymeleaf templates
│   │       │   ├── login.html
│   │       │   └── routes/
│   │       │       ├── index.html
│   │       │       ├── simple-route-form.html
│   │       │       ├── advance-route-form.html
│   │       │       └── route-details.html
│   │       └── application.yml  # Main configuration
│   └── test/java/com/doors/gateway/
│       └── GatewayApplicationTests.java
├── target/                     # Build output (compiled classes, JARs)
├── pom.xml                     # Maven build configuration
├── mvnw, mvnw.cmd              # Maven wrapper scripts
├── README.md
├── HELP.md
├── QUICKSTART.md
└── EXAMPLES.md

```

## How It Works

1. **Static Routes**: Configured in `application.yml`, loaded at startup
2. **Dynamic Routes**: Created through web UI or REST API, stored in memory
3. **Route Refresh**: Automatic refresh when routes are added/updated/deleted
4. **Dual Stack**: Uses WebFlux for Gateway and Web MVC for Thymeleaf UI

## Monitoring

Access Spring Boot Actuator endpoints:

- Gateway routes: `http://localhost:8080/actuator/gateway/routes`
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`

## Tips

1. **Load Balancing**: Use `lb://service-name` as URI for load-balanced services
2. **Order Matters**: Lower order numbers have higher priority
3. **Path Predicates**: Use `/**` for wildcard matching
4. **Testing**: Use the example route to test with httpbin.org

## Troubleshooting

### Routes not working
- Check the route configuration in the web UI
- Verify the target service is running
- Check logs for any errors

### Web UI not accessible
- Ensure the application is running on port 8080
- Check if another application is using the port

### Changes not reflecting
- Routes should refresh automatically
- Check the logs for refresh events

## License

This project is open source and available under the MIT License.

## Author

Created with ❤️ using Spring Cloud Gateway and Thymeleaf
