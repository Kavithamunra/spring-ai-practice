# Simple Chat AI Application

A Spring Boot-based chat application that demonstrates different approaches to interacting with OpenAI's GPT models. The application provides three distinct chat interfaces, each with varying levels of context awareness and functionality.

## Features

1. **Stateless Chat**
   - Each query is treated independently with no memory of previous interactions
   - Simple question-answer format
   - Endpoint: `/no-state-query`

2. **Context-Aware Chat**
   - Maintains conversation history (last 2 messages)
   - Remembers previous interactions within the same session
   - Endpoint: `/state-query`

3. **Smart Chat with Tools**
   - Maintains conversation history
   - Can use additional tools (like InformationDesk)
   - System prompt for better responses
   - Endpoint: `/smart-query`

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- OpenAI API key

## Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd simple-chat-ai
   ```

2. Set up your OpenAI API key:
   - Create a `.env` file in the project root
   - Add your OpenAI API key:
     ```
     OPENAI_API_KEY=your-api-key-here
     ```

3. Build the application:
   ```bash
   mvn clean install
   ```

## Running the Application

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

## Usage

### Web Interface
- The web interface is available at `http://localhost:8080`
- Use the radio buttons to switch between different chat modes:
  - **No-State**: Each query is independent
  - **State**: Maintains conversation context
  - **Smart**: Uses additional tools and system prompts

### API Endpoints

#### 1. Stateless Chat
```http
GET /no-state-query?query=Your message here
```

#### 2. Context-Aware Chat
```http
GET /state-query?query=Your message here
```

#### 3. Smart Chat with Tools
```http
GET /smart-query?query=Your message here
```

## Configuration

Configuration options can be modified in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.model=gpt-4

# Logging
logging.level.org.springframework.ai=INFO
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/chatbot/
│   │       ├── controller/               # REST controllers
│   │       │   ├── OpenAiContextAwareController.java
│   │       │   ├── OpenAiSmartController.java
│   │       │   └── OpenAiStatelessController.java
│   │       └── tools/                    # Custom tools
│   │           └── InformationDesk.java
│   └── resources/
│       ├── static/                       # Frontend resources
│       │   └── index.html
│       └── application.properties        # Application configuration
└── test/                                 # Test files
```

## Dependencies

- Spring Boot 3.2.0
- Spring AI (for OpenAI integration)
- Spring Web
- Lombok (for reducing boilerplate code)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with [Spring AI](https://spring.io/projects/spring-ai)
- Uses [OpenAI's GPT models](https://platform.openai.com/docs/models)
