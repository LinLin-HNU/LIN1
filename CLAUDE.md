# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot-based food delivery management system called "苍穹外卖" (Sky Take-Out). The system is designed for restaurant operations management with features including dish management, order processing, user management, and AI-powered chat functionality.

## Architecture

The project follows a multi-module Maven structure:

- **sky-common**: Shared utilities, constants, and common configurations
- **sky-pojo**: Data Transfer Objects (DTOs) and entity classes
- **sky-server**: Main application module containing all business logic

### Key Technologies
- Spring Boot 2.7.3 with transaction management and caching
- MyBatis for database operations with MySQL
- Redis for caching
- JWT for authentication
- LangChain4j for AI chat functionality
- Aliyun OSS for file storage
- WeChat Pay integration
- Apache POI for Excel operations

### Module Structure
- **Controllers**: Split into admin and user packages for different user roles
- **Services**: Business logic layer with corresponding mapper interfaces
- **Mappers**: MyBatis mappers with XML configurations
- **Entities**: Database entities following JPA conventions
- **DTOs**: Data transfer objects for API communication

## Development Commands

### Build and Run
```bash
# Build the entire project
mvn clean install

# Build specific module
mvn clean install -pl sky-server

# Run the application
mvn spring-boot:run -pl sky-server

# Run with specific profile
mvn spring-boot:run -pl sky-server -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl sky-server

# Run specific test class
mvn test -Dtest=Test1
```

### Database
- MySQL database: `sky_take_out`
- Default connection: `localhost:3306`
- Redis: `localhost:6379`

## Configuration

### Environment Profiles
- **dev**: Development configuration (default)
- **prod**: Production configuration (not present in repo)

### Key Configuration Files
- `application.yml`: Main configuration
- `application-dev.yml`: Development-specific settings
- Database and Redis configurations are externalized for security

## AI Features

The system includes AI chat functionality powered by LangChain4j and Qwen Plus model:
- AIController handles chat API endpoints
- AIService manages AI conversation logic
- AITools provides utility functions for AI operations
- Chat memory is configured to retain recent 20 messages

## Security Notes

- JWT tokens are used for authentication (admin and user endpoints)
- Database credentials and API keys are externalized to environment variables
- Sensitive information should not be committed to version control