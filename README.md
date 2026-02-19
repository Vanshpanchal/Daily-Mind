# Daily Mind - Journaling Application 📔

Daily Mind is a comprehensive Spring Boot-based journaling application that enables users to create, manage, and analyze their daily journal entries with AI-powered insights. The application combines secure authentication, intelligent sentiment analysis, automated tagging, and various AI services to enhance the journaling experience.

## ✨ Core Features

### 🔐 Authentication & User Management
*   **JWT Authentication**: Secure token-based authentication system
*   **User Registration & Login**: Simple signup and login with username/password
*   **OAuth Integration**: Google OAuth and GitHub OAuth support for seamless authentication
*   **Password Management**: 
    *   Update password functionality
    *   OTP-based password reset via email
    *   OTP verification system
*   **User Profile Management**: View and update user profiles
*   **Role-Based Access Control**: User and Admin role management
*   **Account Deletion**: Self-service account deletion

### 📝 Journal Entry Management
*   **CRUD Operations**: Create, read, update, and delete journal entries
*   **Pagination**: Efficiently browse entries with customizable page size (default: 10, max: 50)
*   **Full-Text Search**: Search across journal titles, content, and tags with Redis caching
*   **Date Filtering**: Filter journal entries by date range (start date and end date)
*   **Auto-Timestamping**: Automatic timestamp tracking for all entries
*   **Sort by Date**: Entries sorted by date in descending order

### 🤖 AI-Powered Features
*   **Sentiment Analysis**: 
    *   Analyze emotional tone of journal entries using Azure Cognitive Services
    *   Weekly sentiment reports via email
    *   Toggle sentiment analysis on/off per user
    *   Redis caching for sentiment results (7-hour TTL)
*   **Auto-Tagging**: 
    *   AI-powered automatic tag extraction from journal content
    *   Key phrase extraction using Azure Text Analytics
    *   Intelligent tag refinement using Google Gemini AI
    *   2-3 most relevant tags per entry
*   **Text-to-Speech (TTS)**: 
    *   Convert journal entries to audio using ElevenLabs API
    *   Natural voice reading of title and content
    *   Audio returned as MP3 format
*   **Email Draft Generation**: 
    *   AI-generated weekly sentiment emails using Google Gemini
    *   HTML-formatted emails with sentiment-based color coding
    *   Personalized insights and suggestions

### 🌦️ Weather Integration
*   **Weather Data**: Integration with WeatherStack API
*   **User Greetings**: Personalized greetings with current weather information
*   **Redis Caching**: Weather data cached for 5 minutes for performance
*   **Mood Correlation Potential**: Weather data available for mood analysis

### 📧 Communication & Notifications
*   **Email Service**: Spring Boot Mail integration
*   **HTML Email Support**: Rich HTML email templates
*   **OTP System**: 
    *   Generate and send OTP via email
    *   OTP verification for password reset
*   **Scheduled Weekly Emails**: 
    *   Automated weekly sentiment analysis reports
    *   AI-generated personalized content based on recent journal entries

### 🎵 Audio & Transcription
*   **Audio File Upload**: Support for multipart/form-data audio uploads
*   **File Validation**: Content type and file size validation
*   **Appwrite Integration**: Cloud storage for audio files
*   **Transcription Service**: Infrastructure for audio-to-text conversion

### ⚡ Performance & Caching
*   **Redis Caching**: Reactive Redis integration for high performance
    *   Weather data caching (5 minutes)
    *   Sentiment analysis caching (7 hours)
    *   Search results caching (1 hour)
*   **Application Cache**: In-memory configuration cache with reload capability
*   **Optimized Queries**: MongoDB indexed queries for fast retrieval

### 🛡️ Security & Privacy
*   **Spring Security**: Comprehensive security framework
*   **Password Encryption**: BCrypt password hashing
*   **JWT Token Security**: Secure token generation and validation
*   **Role-Based Endpoints**: Protected admin and user endpoints
*   **Master Key Protection**: Configuration reload protected by master key

## 🛠️ Tech Stack

*   **Java**: 17+
*   **Framework**: Spring Boot 3.4.12
*   **Database**: MongoDB (Document-based NoSQL)
*   **Caching**: Redis (Reactive)
*   **Security**: Spring Security, JWT (JJWT 0.12.5)
*   **Email**: Spring Boot Mail
*   **Documentation**: Swagger UI / OpenAPI 3.0 (SpringDoc 2.5.0)
*   **Build Tool**: Maven
*   **AI Services**:
    *   Azure Cognitive Services (Sentiment Analysis & Key Phrase Extraction)
    *   Google Gemini API (Email generation & Tag refinement)
    *   ElevenLabs API (Text-to-Speech)
*   **External APIs**:
    *   WeatherStack API (Weather data)
    *   Appwrite (Audio file storage)
    *   Google OAuth & GitHub OAuth

## 📊 Data Models

### JournalEntry
*   `id`: ObjectId (MongoDB generated)
*   `title`: String (Entry title)
*   `content`: String (Entry content)
*   `date`: LocalDateTime (Auto-generated timestamp)
*   `tags`: List<String> (AI-generated tags)

### Users
*   `id`: ObjectId (MongoDB generated)
*   `username`: String (Unique, indexed)
*   `password`: String (BCrypt hashed)
*   `email`: String
*   `roles`: List<String> (User roles)
*   `journalEntries`: List<JournalEntry> (DBRef to journal entries)
*   `sentimentAnalysis`: Boolean (Feature toggle)
*   `date`: LocalDateTime (Account creation date)

## Prerequisites

Before running the application, ensure you have the following installed:

*   Java Development Kit (JDK) 17 or higher
*   Maven
*   MongoDB (running locally or accessible via connection string)
*   Redis (running locally or accessible via connection string)

## ⚙️ Configuration

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd Daily-Mind
    ```

2.  **Configure Application Properties**:
    
    Copy `application.properties.example` to `application.properties`:
    ```bash
    cp src/main/resources/application.properties.example src/main/resources/application.properties
    ```

    Update `src/main/resources/application.properties` with your credentials:

    ```properties
    # MongoDB Configuration
    spring.data.mongodb.uri=mongodb://localhost:27017/daily_mind
    
    # Redis Configuration
    spring.data.redis.host=localhost
    spring.data.redis.port=6379
    
    # JWT Configuration
    jwt.secret=your-secret-key-here
    jwt.expiration=86400000
    
    # Email Configuration (for OTP)
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your-email@gmail.com
    spring.mail.password=your-app-password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    
    # Azure Cognitive Services (Sentiment Analysis & Key Phrase Extraction)
    sentiment.analysis.key=your-azure-cognitive-services-key
    sentiment.analysis.endpoint=https://your-region.api.cognitive.microsoft.com/
    
    # Google Gemini API (AI Email Generation)
    gemini.api.key=your-gemini-api-key
    
    # ElevenLabs API (Text-to-Speech)
    elevenlabs.api.key=your-elevenlabs-api-key
    
    # WeatherStack API
    weather.api.key=your-weatherstack-api-key
    
    # Appwrite (Audio Storage)
    appwrite.project.id=your-appwrite-project-id
    appwrite.api.key=your-appwrite-api-key
    appwrite.endpoint=https://cloud.appwrite.io/v1
    
    # Google OAuth
    google.oauth.client.id=your-google-oauth-client-id
    google.oauth.client.secret=your-google-oauth-client-secret
    
    # GitHub OAuth
    github.oauth.client.id=your-github-oauth-client-id
    github.oauth.client.secret=your-github-oauth-client-secret
    
    # Master Key (for config reload)
    MasterKey=your-master-key
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

### 📋 API Endpoint Categories

#### 🌐 Public Endpoints (No Authentication Required)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/public/signup` | Register a new user | `{"username": "user", "password": "pass", "email": "user@email.com"}` |
| `POST` | `/public/login` | Authenticate and receive JWT | `{"username": "user", "password": "pass"}` |
| `GET` | `/public/healthcheck` | Application health check | - |
| `GET` | `/public/oauth/google/callback` | Google OAuth callback | Query param: `code` |
| `POST` | `/public/gemini-test` | Test Gemini AI email draft | Plain text prompt |
| `GET` | `/public/check` | Trigger weekly email job (testing) | - |

#### 📔 Journal Endpoints (Requires Bearer Token)

| Method | Endpoint | Description | Params/Body |
|--------|----------|-------------|-------------|
| `GET` | `/journal` | List user's journal entries (paginated) | `?page=0&size=10` |
| `POST` | `/journal/` | Create new journal entry | `{"title": "...", "content": "..."}` |
| `POST` | `/journal/id/{myId}` | Get journal entry by ID | Path: ObjectId |
| `PUT` | `/journal/id/{myId}` | Update journal entry | `{"title": "...", "content": "..."}` |
| `DELETE` | `/journal/id/{myId}` | Delete journal entry | Path: ObjectId |
| `GET` | `/journal/{id}/audio` | Get TTS audio of entry (MP3) | Path: ObjectId |
| `GET` | `/journal/search` | Search journal entries | `?query=keyword` |
| `GET` | `/journal/filter/` | Filter entries by date range | `?startDate=2024-01-01&endDate=2024-12-31` |

#### 👤 User Endpoints (Requires Bearer Token)

| Method | Endpoint | Description | Params/Body |
|--------|----------|-------------|-------------|
| `GET` | `/user/me` | Get current user profile | - |
| `PUT` | `/user` | Update username/password | `{"username": "new", "password": "new"}` |
| `DELETE` | `/user` | Delete current user account | - |
| `GET` | `/user/greet` | Greeting with weather info | - |
| `GET` | `/user/setSentimentAnalysis` | Toggle sentiment analysis | - |
| `POST` | `/user/updatePassword` | Update password only | Plain text password |
| `GET` | `/user/resetPasswordOtp` | Request password reset OTP | - |
| `GET` | `/user/verifyOtp` | Verify OTP | `?otp=123456` |
| `POST` | `/user/upload-audio` | Upload audio file | Multipart file: `file` |

#### 👨‍💼 Admin Endpoints (Requires Admin Role)

| Method | Endpoint | Description | Params/Body |
|--------|----------|-------------|-------------|
| `GET` | `/admin/all-users` | List all users in system | - |

#### 🔧 Utility Endpoints (Requires Bearer Token + Master Key)

| Method | Endpoint | Description | Headers |
|--------|----------|-------------|---------|
| `GET` | `/utils/config` | Reload application configuration | `X-ADMIN-KEY: {masterKey}` |

## 🏗️ Architecture & Design Patterns

### Layered Architecture
*   **Controller Layer**: RESTful API endpoints with Swagger documentation
*   **Service Layer**: Business logic and AI service integration
*   **Repository Layer**: MongoDB data access with custom implementations
*   **Entity Layer**: Domain models with Lombok annotations
*   **DTO Layer**: Data transfer objects for API requests/responses
*   **Filter Layer**: JWT authentication filter
*   **Utility Layer**: Helper classes and constants
*   **Cache Layer**: Redis caching and application configuration cache

### Key Design Patterns
*   **Dependency Injection**: Spring Boot autowiring
*   **Repository Pattern**: Spring Data MongoDB
*   **DTO Pattern**: Separation of domain and API models
*   **Builder Pattern**: User entity construction
*   **Service Layer Pattern**: Business logic encapsulation
*   **Filter Pattern**: JWT authentication filter chain

### Security Architecture
*   JWT token-based stateless authentication
*   BCrypt password encryption
*   Role-based access control (RBAC)
*   Security filter chain for endpoint protection
*   OAuth 2.0 integration for third-party authentication

## 🚀 Advanced Features

### Intelligent Caching Strategy
*   **Weather Data**: 5-minute TTL (300 seconds)
*   **Sentiment Analysis**: 7-hour TTL (25,200 seconds)
*   **Search Results**: 1-hour TTL (3,600 seconds)
*   **Configuration**: In-memory cache with manual reload capability

### AI Service Integration Flow
1. **Journal Creation** → Auto-tagging service extracts key phrases
2. **Weekly Schedule** → Sentiment analysis on last 11 days of entries
3. **Email Generation** → AI generates personalized HTML email
4. **TTS Request** → ElevenLabs converts entry to natural speech

### Scheduled Jobs
*   Weekly sentiment analysis email reports (configurable cron)
*   Automated user engagement tracking
*   Sentiment trend analysis

## 📈 Monitoring & Maintenance

### Health Checks
*   Application health endpoint: `/public/healthcheck`
*   Returns "healthy" status for monitoring tools

### Configuration Management
*   Dynamic configuration reload without restart
*   Master key-protected configuration endpoint
*   Application cache for API URLs and settings

### Logging
*   Logback configuration for structured logging
*   SLF4J with Lombok `@Slf4j` annotation
*   Detailed error tracking and debugging

## 🔮 Future Enhancement Suggestions

Based on the existing architecture, here are recommended AI service additions:

### 1. 🎤 **Voice-to-Text Journaling** (HIGH PRIORITY)
*   **Technology**: OpenAI Whisper, Google Speech-to-Text, or AssemblyAI
*   **Why**: Complements existing audio upload feature
*   **Features**: Real-time transcription, multi-language support, speaker diarization
*   **Use Case**: Record journal entries on-the-go by speaking

### 2. 💡 **Smart Journal Prompts** (HIGH PRIORITY)
*   **Technology**: OpenAI GPT-4, Google Gemini
*   **Why**: Increase user engagement and overcome writer's block
*   **Features**: Contextual daily prompts based on mood trends, guided reflection questions
*   **Use Case**: "You seemed stressed yesterday, how are you feeling today?"

### 3. 🔍 **RAG-based Journal Chat** (MEDIUM PRIORITY)
*   **Technology**: LangChain + Vector Database (Pinecone, Weaviate)
*   **Why**: Enable conversational queries about past entries
*   **Features**: Ask questions like "When was I last happy?" or "What did I do in July?"
*   **Use Case**: Conversational memory search

### 4. 📊 **Advanced Analytics Dashboard** (MEDIUM PRIORITY)
*   **Technology**: Chart.js, D3.js for visualization
*   **Features**: 
    *   Mood vs. Weather correlation graphs
    *   Word clouds of frequently used terms
    *   Sentiment trend line over time
    *   Monthly/yearly summaries

### 5. 🎮 **Gamification System** (LOW PRIORITY)
*   **Features**: Streak tracking, achievement badges, journaling milestones
*   **Benefits**: Increase user retention and consistent journaling habits

### 6. 🔒 **End-to-End Encryption** (SECURITY PRIORITY)
*   **Technology**: Client-side encryption before storage
*   **Why**: Enhanced privacy for sensitive journal content
*   **Features**: Zero-knowledge encryption, encrypted backups

### 7. 📥 **Data Export (GDPR Compliance)** (COMPLIANCE PRIORITY)
*   **Formats**: JSON, PDF, ZIP with all entries and media
*   **Why**: User data portability and legal compliance
*   **Features**: Complete data download with formatting

### 8. 🖼️ **Multimedia Journal Entries** (FEATURE EXPANSION)
*   **Technology**: AWS S3, GridFS, or Appwrite
*   **Features**: Attach photos, videos, voice memos to entries
*   **Use Case**: Richer journaling experience with visual memories

### 9. 🧠 **Mental Health Insights** (WELLNESS PRIORITY)
*   **Technology**: Specialized sentiment analysis models
*   **Features**: 
    *   Detect patterns of stress, anxiety, or depression
    *   Gentle suggestions for mental wellness
    *   Professional resource recommendations (if patterns detected)

### 10. 🌍 **Multi-language Support** (INTERNATIONALIZATION)
*   **Technology**: Google Translate API, i18n
*   **Features**: Journal in any language, auto-translation of AI features
*   **Use Case**: Global user base expansion

## 📝 Development Notes

### Code Quality
*   Uses Lombok for boilerplate reduction
*   Comprehensive Swagger/OpenAPI documentation
*   SonarQube integration for code analysis
*   Test coverage tracking

### Best Practices
*   RESTful API design principles
*   Consistent error handling with `ApiResponse` wrapper
*   Standardized HTTP status codes
*   Proper separation of concerns

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the terms specified in the repository.

## 👥 Support

For issues, questions, or contributions, please use the GitHub issue tracker.

---

**Last Updated**: February 18, 2026

## 📖 Example Usage

### Authentication Flow
```bash
# 1. Register a new user
curl -X POST http://localhost:8080/public/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"john@example.com","password":"secure123","email":"john@example.com"}'

# 2. Login and get JWT token
curl -X POST http://localhost:8080/public/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john@example.com","password":"secure123"}'

# Response: {"status":"success","message":"Login Done","data":"eyJhbGc..."}
```

### Create and Manage Journal Entries
```bash
# 3. Create a journal entry (use token from login)
curl -X POST http://localhost:8080/journal/ \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Amazing Day","content":"Today was incredible! I learned so much about AI and journaling."}'

# 4. Get all your journal entries (paginated)
curl -X GET "http://localhost:8080/journal?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 5. Search journal entries
curl -X GET "http://localhost:8080/journal/search?query=amazing" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 6. Get audio reading of a journal entry
curl -X GET "http://localhost:8080/journal/{ENTRY_ID}/audio" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output journal_audio.mp3
```

### AI Features Usage
```bash
# Toggle sentiment analysis
curl -X GET http://localhost:8080/user/setSentimentAnalysis \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get weather greeting
curl -X GET http://localhost:8080/user/greet \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🎯 Project Structure
```
Daily-Mind/
├── src/
│   ├── main/
│   │   ├── java/spring/project/Daily/Mind/
│   │   │   ├── DailyMindApplication.java        # Main application entry point
│   │   │   ├── cache/
│   │   │   │   └── AppCache.java                # Configuration caching
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java           # Swagger/OpenAPI setup
│   │   │   │   ├── RedisConfig.java             # Redis configuration
│   │   │   │   └── SecurityConfig.java          # Security & JWT setup
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java         # Admin endpoints
│   │   │   │   ├── JournalEntryController.java  # Journal CRUD
│   │   │   │   ├── PublicController.java        # Auth & public endpoints
│   │   │   │   ├── UserController.java          # User management
│   │   │   │   └── UtilityController.java       # Maintenance tools
│   │   │   ├── DTO/                             # Data Transfer Objects
│   │   │   ├── entity/
│   │   │   │   ├── JournalEntry.java            # Journal model
│   │   │   │   └── Users.java                   # User model
│   │   │   ├── filter/                          # JWT authentication filter
│   │   │   ├── repository/                      # MongoDB repositories
│   │   │   ├── responseDTO/                     # API response models
│   │   │   ├── service/
│   │   │   │   ├── AiService.java               # AI integrations (Gemini, ElevenLabs)
│   │   │   │   ├── AutoTaggingService.java      # AI tag extraction
│   │   │   │   ├── JournalEntryService.java     # Journal business logic
│   │   │   │   ├── MailService.java             # Email service
│   │   │   │   ├── OtpService.java              # OTP generation/verification
│   │   │   │   ├── RedisService.java            # Redis caching
│   │   │   │   ├── SchedulingService.java       # Scheduled jobs
│   │   │   │   ├── SentimentAnalysisService.java # Sentiment analysis
│   │   │   │   ├── TranscriptionService.java    # Audio transcription
│   │   │   │   ├── UserService.java             # User business logic
│   │   │   │   └── WeatherService.java          # Weather API integration
│   │   │   └── utility/
│   │   │       ├── ApiResponse.java             # Standard API response wrapper
│   │   │       ├── Constants.java               # Application constants
│   │   │       └── JwtUtils.java                # JWT token utilities
│   │   └── resources/
│   │       ├── application.properties           # Configuration file
│   │       ├── application.properties.example   # Example configuration
│   │       └── logback.xml                      # Logging configuration
│   └── test/                                    # Unit and integration tests
├── pom.xml                                      # Maven dependencies
├── README.md                                    # This file
├── AI_SERVICES_SUGGESTIONS.md                   # AI enhancement ideas
└── DAILY_MIND_STRATEGY.md                       # Project strategy

```

## Execution Commands

### Development
```bash
# Build and run tests
mvn clean install

# Run application in development mode
mvn spring-boot:run

# Run tests only
mvn test

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production
```bash
# Package application
mvn package

# Run JAR file
java -jar target/Daily-Mind-0.0.1-SNAPSHOT.jar

# Run with external configuration
java -jar target/Daily-Mind-0.0.1-SNAPSHOT.jar --spring.config.location=file:/path/to/application.properties
```

### Code Quality
```bash
# Run SonarQube analysis
mvn sonar:sonar

# Generate test coverage report
mvn clean test jacoco:report
```

## 🔧 Troubleshooting

### Common Issues

**Issue**: MongoDB connection refused
```bash
# Solution: Ensure MongoDB is running
mongod --dbpath /path/to/data/db
```

**Issue**: Redis connection error
```bash
# Solution: Start Redis server
redis-server
```

**Issue**: JWT token expired
```bash
# Solution: Login again to get a new token
# Tokens expire after 24 hours by default (configurable in application.properties)
```

**Issue**: AI service API key errors
```bash
# Solution: Verify all API keys are correctly set in application.properties
# Check that keys have proper permissions and quotas
```

## 📚 Additional Resources

*   [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
*   [MongoDB Documentation](https://docs.mongodb.com/)
*   [Redis Documentation](https://redis.io/documentation)
*   [JWT.io](https://jwt.io/) - JWT token debugger
*   [Azure Cognitive Services](https://azure.microsoft.com/en-us/services/cognitive-services/)
*   [Google Gemini API](https://ai.google.dev/)
*   [ElevenLabs API](https://elevenlabs.io/docs)

---

**Built with ❤️ using Spring Boot**

**Last Updated**: February 18, 2026

