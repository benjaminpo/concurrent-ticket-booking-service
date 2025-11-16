# Concurrent Ticket Booking Service 🎟️

A full-stack application demonstrating concurrent ticket booking with Spring Boot backend and Angular frontend. Includes Docker deployment and comprehensive concurrent testing.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red.svg)](https://angular.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-Educational-yellow.svg)](#license)

## Features

### Backend (Spring Boot)
- ✅ **Concurrent Booking Handling**: Uses pessimistic locking to prevent overbooking
- ✅ **RESTful API**: Clean endpoints for booking and retrieving event information
- ✅ **H2 In-Memory Database**: Fast, lightweight database for development
- ✅ **Comprehensive Testing**: JUnit tests simulating concurrent booking scenarios
- ✅ **Error Handling**: Global exception handling with meaningful error messages
- ✅ **Optimistic & Pessimistic Locking**: Version-based and database-level locking strategies

### Frontend (Angular 17+)
- ✅ **Modern UI**: Built with Angular Material for a clean, responsive design
- ✅ **Event List Component**: Displays all events in a material table
- ✅ **Booking Dialog**: Modal dialog for booking tickets with validation
- ✅ **Real-time Updates**: Automatic refresh after successful bookings
- ✅ **User Feedback**: Success/error notifications with snackbars
- ✅ **Proxy Configuration**: Seamless communication with backend API

### Docker & Deployment
- 🐳 **Multi-stage Builds**: Optimized Docker images (~630MB total)
- 🍎 **Apple Silicon Support**: Native ARM64 compatibility for M1/M2/M3 Macs
- 🔒 **Security**: Non-root users, health checks, minimal attack surface
- ⚡ **Production-Ready**: Nginx reverse proxy with compression and caching

## Architecture

### Backend Architecture
```
backend/
├── src/main/java/com/geoplace/ticketbooking/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA entities
│   ├── exception/      # Custom exceptions & handlers
│   ├── repository/     # JPA repositories
│   └── service/        # Business logic
└── src/test/           # JUnit tests
```

### Frontend Architecture
```
frontend/
└── src/app/
    ├── components/     # Angular components
    │   ├── event-list/
    │   └── book-dialog/
    ├── models/         # TypeScript interfaces
    └── services/       # HTTP services
```

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Lombok
- JUnit 5

### Frontend
- Angular 17
- Angular Material
- TypeScript 5.4
- RxJS 7.8

## Getting Started

### Prerequisites

#### Option 1: Docker (Recommended)
- Docker 20.10+
- Docker Compose 2.0+

#### Option 2: Local Development
- **Java 21** (required - see installation below)
- Maven 3.6+
- Node.js 18+ and npm
- Git

> **⚠️ Important**: This project requires **Java 21 specifically**. Java 21, 25, or other versions may cause compilation issues with Lombok and Spring Boot 3.2.0.

### Installing Java 21

#### macOS (using Homebrew)
```bash
# Install Java 21
brew install openjdk@21

# Set JAVA_HOME permanently (add to ~/.zshrc or ~/.bash_profile)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version  # Should show Java 21.x.x
```

#### Linux (Ubuntu/Debian)
```bash
# Install Java 21
sudo apt update
sudo apt install openjdk-17-jdk

# Set JAVA_HOME permanently (add to ~/.bashrc)
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc

# Verify installation
java -version  # Should show Java 21.x.x
```

#### Windows
Download and install from [Adoptium](https://adoptium.net/temurin/releases/?version=17) or [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

After installation, set `JAVA_HOME` environment variable to point to your Java 21 installation directory.

### Quick Start with Docker 🐳

The easiest way to run the application:

```bash
# Start everything with one command
./docker-start.sh
```

Or manually with docker-compose:

```bash
# Build and start containers
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop containers
docker-compose down
```

Access the application:
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

**Apple Silicon Users (M1/M2/M3)**: Fully supported! Docker uses native ARM64 images for optimal performance.

### Running Locally (Without Docker)

#### Running the Backend

> **Note**: Ensure Java 21 is installed and `JAVA_HOME` is set correctly (see Prerequisites section above).

1. Navigate to the backend directory:
```bash
cd backend
```

2. Verify Java version:
```bash
java -version  # Must show Java 21
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

5. Access H2 Console:
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:ticketdb`
- **Username**: `sa`
- **Password**: (leave blank)

### Running the Frontend

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:4200`

### Running Tests

#### Backend Tests

**Important**: Tests must be run locally (Maven not available in Docker runtime)

```bash
# Local execution
cd backend
mvn test

# Or use the helper script
./run-tests.sh
```

To run tests during Docker build:
```bash
# Edit backend/Dockerfile and remove -DskipTests flag
# Then rebuild:
docker-compose build backend
```

The test suite includes:
- ✅ Basic booking functionality tests
- ✅ Concurrent booking tests (20 threads competing for 100 tickets)
- ✅ High contention tests (50 threads competing for 10 tickets)
- ✅ Multi-event concurrent booking tests
- ✅ Varying ticket count tests

## API Endpoints

### Get All Events
```
GET /api/events
```
Returns a list of all available events.

### Get Event by ID
```
GET /api/tickets/{id}
```
Returns details of a specific event including remaining tickets.

### Book Tickets
```
POST /api/tickets/{id}/book?count=2&userId=user123
```
Books tickets for an event. Returns booking confirmation or error.

**Parameters:**
- `id` (path): Event ID
- `count` (query): Number of tickets to book
- `userId` (query): User identifier (default: "anonymous")

**Success Response (201):**
```json
{
  "bookingId": 1,
  "eventId": 1,
  "eventName": "Spring Boot Conference 2025",
  "ticketsBooked": 2,
  "remainingTickets": 98,
  "message": "Booking successful"
}
```

**Error Response (409 - Insufficient Tickets):**
```json
{
  "timestamp": "2025-11-15T10:30:00",
  "message": "Insufficient Tickets",
  "details": "Not enough tickets available. Requested: 5, Available: 2"
}
```

## Concurrency Handling

The application uses **pessimistic locking** to handle concurrent bookings:

1. **Database-Level Locking**: Uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` on the repository method
2. **Transaction Management**: All booking operations are wrapped in transactions
3. **Version Control**: `@Version` annotation on entities for optimistic locking fallback

This ensures that:
- Multiple users can book tickets simultaneously
- No overbooking occurs
- Failed bookings receive immediate feedback
- The system maintains data integrity under high load

## Sample Events

The application initializes with 5 sample events:
1. Spring Boot Conference 2025 (100 tickets)
2. Angular Workshop (50 tickets)
3. Java 21 Masterclass (75 tickets)
4. Microservices Summit (120 tickets)
5. Cloud Native Conference (200 tickets)

## Key Features Demonstrated

### Backend
- ✅ Pessimistic locking for concurrency control
- ✅ Global exception handling
- ✅ RESTful API design
- ✅ Data validation
- ✅ Comprehensive unit tests
- ✅ Clean architecture with separation of concerns

### Frontend
- ✅ Standalone components (Angular 17 approach)
- ✅ Reactive programming with RxJS
- ✅ Material Design implementation
- ✅ Form validation
- ✅ HTTP interceptors via proxy
- ✅ User feedback with snackbars

## Testing Concurrent Scenarios

The test suite includes several concurrent booking scenarios:

1. **No Overbooking Test**: 20 threads trying to book 5 tickets each from 100 available
2. **High Contention Test**: 50 threads competing for only 10 tickets
3. **Multi-Event Test**: Concurrent bookings across multiple events
4. **Varying Counts Test**: Threads booking different quantities simultaneously

Run these tests with:
```bash
cd backend
mvn test -Dtest=TicketBookingServiceTest
```

## Docker Details

### Image Sizes
| Component | Size | Architecture |
|-----------|------|--------------|
| Backend | ~450MB | ARM64/AMD64 |
| Frontend | ~180MB | ARM64/AMD64 |
| **Total** | **~630MB** | Multi-platform |

### Container Features
- ✅ **Health Checks**: Automatic restart on failure
- ✅ **Non-root Users**: Enhanced security
- ✅ **Optimized Builds**: Multi-stage with layer caching
- ✅ **Nginx Proxy**: Production-ready frontend serving
- ✅ **Gzip Compression**: Reduced bandwidth usage
- ✅ **Security Headers**: X-Frame-Options, X-Content-Type-Options

### Management Commands

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Restart a service
docker-compose restart backend

# Stop everything
docker-compose down

# Clean rebuild
docker-compose down --rmi all
docker-compose up --build -d

# Check container health
docker-compose ps
```

## Quick Reference

### Backend API Examples

```bash
# Get all events
curl http://localhost:8080/api/events

# Get specific event
curl http://localhost:8080/api/tickets/1

# Book tickets
curl -X POST "http://localhost:8080/api/tickets/1/book?count=2&userId=john"
```

### Common Tasks

```bash
# Start everything (Docker)
./docker-start.sh

# Run tests
./run-tests.sh

# Stop Docker
./docker-stop.sh

# View backend logs
docker-compose logs -f backend

# View frontend logs
docker-compose logs -f frontend
```

## Troubleshooting

### Java Version Issues

**Problem**: Compilation fails with "cannot find symbol" errors for Lombok-generated methods.

**Solution**: Ensure you're using Java 21:
```bash
# Check current Java version
java -version

# If not Java 21, set JAVA_HOME (macOS)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Verify it's set correctly
echo $JAVA_HOME
java -version

# Clean and rebuild
cd backend
mvn clean install
```

**Problem**: Multiple Java versions installed, compilation uses wrong version.

**Solution**: Make Java 21 permanent by adding to your shell config:
```bash
# For zsh (macOS default)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc

# For bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.bash_profile
source ~/.bash_profile
```

### Port Already in Use
```bash
# Find and kill process
lsof -ti:4200 | xargs kill -9  # Frontend
lsof -ti:8080 | xargs kill -9  # Backend
```

### Docker Build Fails
```bash
# Clean rebuild
docker-compose down --rmi all
docker system prune -a
./docker-start.sh
```

### Tests Can't Run in Docker
Tests require Maven, which is only in the build stage. Run locally:
```bash
cd backend && mvn test
```

### H2 Console Access Denied
Make sure you're using:
- JDBC URL: `jdbc:h2:mem:ticketdb`
- Username: `sa`
- Password: (empty)

## Project Structure

```
GeoPlace-concurrent-ticket-booking-service/
├── backend/                    # Spring Boot application
│   ├── src/
│   │   ├── main/java/         # Source code
│   │   └── test/java/         # Concurrent tests
│   ├── Dockerfile             # Production Docker image
│   ├── Dockerfile.test        # Test Docker image
│   └── pom.xml                # Maven dependencies
│
├── frontend/                   # Angular application
│   ├── src/app/               # Application code
│   ├── Dockerfile             # Production Docker image
│   ├── nginx.conf             # Nginx configuration
│   └── package.json           # npm dependencies
│
├── docker-compose.yml         # Orchestration
├── docker-start.sh            # Start script
├── docker-stop.sh             # Stop script
├── run-tests.sh               # Test script
└── README.md                  # This file
```

## Performance

- **Startup Time**: ~30 seconds (Docker), ~20 seconds (local)
- **Response Time**: < 50ms per request
- **Concurrency**: Handles 50+ simultaneous bookings
- **Test Coverage**: 7 comprehensive test scenarios (4 concurrent + 3 functional)

## Future Enhancements

- [ ] Add user authentication and authorization
- [ ] Implement booking cancellation
- [ ] Add booking history and reporting
- [ ] Implement email notifications
- [ ] Add payment integration
- [ ] Implement real-time availability updates with WebSockets
- [ ] Add Redis caching for high-traffic scenarios
- [ ] Implement rate limiting
- [ ] Add Kubernetes deployment manifests

## What You'll Learn

This project demonstrates:
- ✅ **Concurrency Control**: Pessimistic locking to prevent race conditions
- ✅ **Transaction Management**: ACID compliance in booking operations
- ✅ **Docker Deployment**: Multi-stage builds and container orchestration
- ✅ **REST API Design**: Clean, RESTful endpoint architecture
- ✅ **Angular Best Practices**: Standalone components, RxJS, Material Design
- ✅ **Testing Strategies**: Concurrent scenario testing with JUnit
- ✅ **Error Handling**: Global exception handling and user feedback

## Support & Documentation

- 📖 **README.md** - This file (main overview)
- 🚀 **QUICKSTART.md** - Get started in 5 minutes (if exists)
- 🐳 **Docker Guide** - Check docker-start.sh for Docker usage
- 🍎 **Apple Silicon** - Native ARM64 support included
- 🧪 **Testing** - See backend/src/test/ for test examples

## Acknowledgments

Built with:
- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [Angular](https://angular.io/) - Frontend framework
- [Angular Material](https://material.angular.io/) - UI components
- [H2 Database](https://www.h2database.com/) - In-memory database
- [Docker](https://www.docker.com/) - Containerization

---

**Ready to start?** Run `./docker-start.sh` and open http://localhost:4200 🚀


