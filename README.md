# Daily Mind - Journaling Application

Daily Mind is a Spring Boot-based journaling application that allows users to create, manage, and analyze their daily journal entries. It features secure authentication, AI-powered insights, and sentiment analysis.

## Features

*   **User Authentication**: Secure signup and login using JWT (JSON Web Tokens).
*   **Journal Management**: Create, read, update, and delete journal entries.
*   **Pagination**: Efficiently browse through journal entries with pagination support.
*   **Search**: Full-text search capability for journal entries.
*   **AI Integration**:
    *   **Sentiment Analysis**: Analyze the sentiment of journal entries.
    *   **Text-to-Speech**: Convert journal entries to audio.
*   **Admin Features**: Administrative endpoints for user management.
*   **Utility**: Configuration reloading and maintenance tools.

## Tech Stack

*   **Java**: 17+
*   **Framework**: Spring Boot 3.x
*   **Database**: MongoDB
*   **Caching**: Redis
*   **Security**: Spring Security, JWT
*   **Documentation**: Swagger UI (OpenAPI 3)
*   **Build Tool**: Maven

## Prerequisites

Before running the application, ensure you have the following installed:

*   Java Development Kit (JDK) 17 or higher
*   Maven
*   MongoDB (running locally or accessible via connection string)
*   Redis (running locally or accessible via connection string)

## Configuration

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd Daily-Mind
    ```

2.  **Configure Application Properties**:
    Update `src/main/resources/application.properties` with your database and API keys.
    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/daily_mind
    spring.data.redis.host=localhost
    spring.data.redis.port=6379
    # Add other necessary configurations (e.g., API keys for AI services)
    ```

## Running the Application

1.  **Build the project**:
    ```bash
    mvn clean install
    ```

2.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## API Documentation

The application provides interactive API documentation using Swagger UI.

*   **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Key Endpoints

#### Public
*   `POST /public/signup`: Register a new user.
    *   **Request Body**: `{"username": "user", "password": "password"}`
*   `POST /public/login`: Authenticate and receive a JWT token.
    *   **Request Body**: `{"username": "user", "password": "password"}`
*   `GET /public/healthcheck`: Check application health.

#### Journal (Requires Bearer Token)
*   `GET /journal`: List current user's journal entries (Paginated).
    *   **Params**: `page` (default 0), `size` (default 10)
*   `POST /journal/`: Create a new journal entry.
    *   **Request Body**: `{"title": "My Day", "content": "Today was great..."}`
*   `GET /journal/id/{myId}`: Get a specific journal entry by ID.
*   `PUT /journal/id/{myId}`: Update a journal entry.
*   `DELETE /journal/id/{myId}`: Delete a journal entry.
*   `GET /journal/search`: Search journal entries.
    *   **Params**: `query`
*   `GET /journal/{id}/audio`: Get audio reading of a journal entry.

#### User (Requires Bearer Token)
*   `GET /user/me`: Get current user profile.
*   `PUT /user`: Update user profile (username/password).

#### Admin (Requires Admin Role)
*   `GET /admin/all-users`: List all users.

## Execution Commands

### Run Tests
```bash
mvn test
```

### Package Application
```bash
mvn package
```

### Run JAR
```bash
java -jar target/Daily-Mind-0.0.1-SNAPSHOT.jar
```

## License

This project is licensed under the MIT License.

