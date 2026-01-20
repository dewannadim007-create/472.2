# Flicker

A text-based microblogging platform built with Spring Boot and MongoDB. Flicker provides a clean, minimalist interface for sharing thoughts and engaging with content through posts, comments, and a voting system.

## Features

- User authentication and authorization with Spring Security
- Create, read, update, and delete posts
- Comment system for discussions
- Upvote and downvote functionality
- User profiles with customizable information
- Real-time content updates
- Responsive web interface using Thymeleaf templates

## Technology Stack

- **Backend Framework**: Spring Boot 3.5.3
- **Database**: MongoDB
- **Security**: Spring Security
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 17

## Project Structure

```
flicker/
├── src/
│   ├── main/
│   │   ├── java/bd/edu/seu/pulse/
│   │   │   ├── controller/     # REST controllers and web endpoints
│   │   │   ├── service/        # Business logic layer
│   │   │   ├── model/          # Data models (User, Post, Comment)
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   └── config/         # Configuration classes
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf HTML templates
│   │       └── application.properties
│   └── test/                   # Unit and integration tests
├── pom.xml                     # Maven configuration
└── DEPLOYMENT.md               # Deployment guide for Render.com
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB instance (local or MongoDB Atlas)

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/dewannadim007-create/472.2.git
   cd 472.2/flicker
   ```

2. Configure MongoDB connection:
   - Copy `.env.example` to `.env`
   - Update the MongoDB connection string in `application.properties` or set environment variable:
     ```
     MONGODB_URI=mongodb://localhost:27017/FLICKER
     ```

3. Build the project:
   ```bash
   ./mvnw clean package
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Access the application at `http://localhost:8080`

## Environment Variables

Configure the following environment variables for deployment:

- `MONGODB_URI`: MongoDB connection string
- `PORT`: Server port (default: 8080)
- `UPLOAD_DIR`: Directory for file uploads (default: /tmp/uploads)

## Deployment

See [DEPLOYMENT.md](flicker/DEPLOYMENT.md) for detailed instructions on deploying to Render.com.

## Development

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package -DskipTests
```

## License

This project is available for educational purposes.

## Contact

For questions or issues, please open an issue on the GitHub repository.
