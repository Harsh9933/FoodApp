# FoodAI Backend

Spring Boot backend for the FoodAI Android app.

## Requirements

- Java 21
- Gradle 8.10+

## Running

### Development (with H2 in-memory database)

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Production (with PostgreSQL)

Set environment variables:
- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `GEMINI_API_KEY` - Google Gemini API key

```bash
./gradlew bootRun
```

## API Endpoints

### Health
- `GET /api/health` - Health check

### Chat
- `POST /api/chat` - Send chat message (SSE streaming)
  - Body: `{ "userId": "string", "conversationId": "string?", "message": "string", "imageBase64": "string?" }`

### Image Analysis
- `POST /api/analyze-image` - Analyze food image (multipart)
- `POST /api/analyze-image/base64` - Analyze food image (base64)

### Preferences
- `GET /api/preferences/{userId}` - Get user preferences
- `POST /api/preferences/{userId}` - Add preference
- `PUT /api/preferences/{userId}/{preferenceId}?active=true/false` - Update preference
- `DELETE /api/preferences/{userId}/{preferenceId}` - Delete preference

### Conversations
- `GET /api/conversations/{userId}` - List conversations
- `GET /api/conversations/{userId}/{conversationId}` - Get conversation with messages
- `DELETE /api/conversations/{userId}/{conversationId}` - Delete conversation