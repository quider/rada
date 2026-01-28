# Rada - School Parent Council Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Open%20Source-blue.svg)]()

A comprehensive web application designed to streamline the management of school parent councils (Rada Rodziców). The system facilitates communication, financial management, voting, and collaboration between parents, students, and school administration.

## 🎯 Features

- **User Management**: Complete user authentication and authorization system with per-user data encryption keys (DEK)
- **Announcements**: Publish and manage school announcements with read tracking
- **Financial Contributions**: Track and manage parent contributions for school targets
- **Polls & Voting**: Create and manage polls with question-answer system
- **Comments System**: Flexible commenting system that can be attached to any entity
- **Notifications**: Real-time notification system for users
- **School & Class Management**: Organize students by schools and classes
- **Student Profiles**: Manage student information and associations
- **Event-Driven Architecture**: Domain events with outbox pattern for reliable inter-domain communication

## 🛠️ Tech Stack

### Backend
- **Java 25** - Latest Java LTS version
- **Spring Boot 4.0.1** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Web MVC** - RESTful API
- **Hibernate** - ORM framework
- **Liquibase** - Database migration and versioning
- **Resilience4j** - Circuit breaker pattern

### Database
- **PostgreSQL** - Primary database
- **UUID** - Primary keys for all entities

### Tools & Libraries
- **Lombok** - Reduce boilerplate code
- **Spring DevTools** - Development productivity
- **Docker Compose** - Container orchestration
- **Maven** - Dependency management

## 📋 Prerequisites

Before running this project, make sure you have installed:

- Java 25 or higher
- Maven 3.6+
- Docker & Docker Compose
- Git

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/quider/rada.git
cd rada
```

### 2. Start the Database

The project uses Docker Compose to run PostgreSQL:

```bash
docker compose up -d
```

This will start a PostgreSQL instance with:
- Database: `rada`
- User: `rada_user`
- Password: `secret`
- Port: `5432`

### 3. Build the Project

```bash
./mvnw clean install
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will be available at: `http://localhost:8080`

## 🗄️ Database Schema

The application uses Liquibase for database migrations. The schema includes:

- **users** - User accounts with authentication details
- **students** - Student records
- **schools** - School information
- **classes** - Class organization
- **announcements** - School announcements
- **contributions** - Financial contributions
- **targets** - Fundraising targets
- **polls_questions** - Poll questions
- **polls_answers** - Poll responses
- **comments** - Comment system
- **comments_associations** - Flexible comment associations
- **notifications** - User notifications

All migrations are located in `src/main/resources/db/changelog/changes/`

## 📁 Project Structure

```
rada/
├── src/
│   ├── main/
│   │   ├── java/pl/factorymethod/rada/
│   │   │   ├── RadaApplication.java       # Main application class
│   │   │   ├── model/                      # JPA entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Student.java
│   │   │   │   ├── School.java
│   │   │   │   ├── Announcement.java
│   │   │   │   ├── Comment.java
│   │   │   │   ├── Contribution.java
│   │   │   │   ├── PollQuestion.java
│   │   │   │   └── ...
│   │   │   └── web/                        # REST controllers
│   │   │       └── AnnouncementsController.java
│   │   └── resources/
│   │       ├── application.yml             # Application configuration
│   │       └── db/changelog/               # Database migrations
│   └── test/                               # Test files
├── compose.yaml                            # Docker Compose configuration
└── pom.xml                                 # Maven dependencies
```

## ⚙️ Configuration

The application can be configured through `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: rada
  datasource:
    url: jdbc:postgresql://localhost:5432/rada
    username: rada_user
    password: secret
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
server:
  port: 8080
```

### Environment Variables

You can override configuration using environment variables:

- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SERVER_PORT` - Application port

## 🧪 Running Tests

```bash
./mvnw test
```

## 🔨 Building for Production

```bash
./mvnw clean package -DskipTests
```

The JAR file will be created in the `target/` directory.

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t rada:latest .
```

### Run with Docker Compose

```bash
docker compose up -d
```

## 📝 API Documentation

The application exposes RESTful endpoints for:

- User management
- Announcements
- Comments
- Contributions
- Polls
- Notifications

Key endpoints:
- **Create user**: `POST /api/v1/users` creates a new user with cryptographically secure DEK (Data Encryption Key) generation for per-user data encryption
- **Open contribution collection**: `POST /api/v1/targets/{targetId}/contributions/open` (requires header `X-Rada-Admin-Token`) freezes per-student fees and emits a domain event (`TargetContributionCollectionOpenedEvent`); a log listener and outbox entry are created for downstream domains.

_(Full API documentation coming soon)_

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👥 Authors

- **FactoryMethod Team** - [pl.factorymethod](https://factorymethod.pl)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community
- All contributors who help improve this project

## 📧 Contact

For questions or support, please open an issue on GitHub or contact the maintainers.

---

Made with ❤️ for school communities
