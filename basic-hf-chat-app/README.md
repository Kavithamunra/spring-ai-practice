# Basic Hugging Face Chat Application

A simple Spring Boot application that integrates with Hugging Face's AI models to provide interactive chat capabilities.

## Features
- Simple REST API for AI-powered chat
- Integration with Hugging Face's AI models
- Configurable API endpoints
- Easy-to-use interface for testing

## Prerequisites
- Java 17 or higher
- Maven 3.6.3 or higher
- A valid Hugging Face API token

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/basic-hf-chat-app.git
cd basic-hf-chat-app
```

### 2. Configure your environment
Create a `.env` file in the root directory with your Hugging Face API token:
```
HUGGINGFACE_API_KEY=your_api_key_here
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Generate AI Response
```
GET /ai/generate?ask=your_message_here
```

#### Example Request
```
GET http://localhost:8080/ai/generate?ask=Tell me a joke
```

#### Example Response
```json
{
    "generation": "<think>\nOkay, the user wants a joke. Let me think of a good one...\n</think>\n\nWhy don't scientists trust atoms?  \nBecause they make up everything!"
}
```

## Testing

### Running Tests
```bash
mvn test
```

### Manual Testing with Postman
1. Open Postman
2. Create a new GET request
3. Enter URL: `http://localhost:8080/ai/generate?ask=Tell me a joke`
4. Send the request

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/example/chatbot/
│   │       ├── controller/    # API endpoints
│   │       ├── service/       # Business logic
│   │       └── config/        # Configuration classes
│   └── resources/
│       └── application.properties  # Application configuration
└── test/                      # Test files
```

## Dependencies
- Spring Boot
- Hugging Face API Client
- Lombok
- Spring Web
- JUnit (for testing)

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
