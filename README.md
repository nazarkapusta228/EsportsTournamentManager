# Esports Tournament Manager

A comprehensive tournament management system for esports competitions, built with Jakarta Persistence (JPA), Hibernate, and PostgreSQL. This project demonstrates Clean Architecture principles, Domain-Driven Design (DDD), and advanced ORM concepts including inheritance strategies, optimistic locking, and transaction management.

## Author

**Nazar Kapustynskyi**

## Features

- **Tournament Management**: Support for both LAN and Online tournaments with distinct characteristics
- **Team Registration**: Manage teams with professional and amateur players
- **Match Scheduling**: Track matches with scores, statuses, and group stage support
- **Player Management**: Handle both professional players (with salaries and sponsors) and amateur players (with rankings and play time)
- **ACID Transactions**: Full transactional support with READ_COMMITTED isolation level
- **Optimistic Locking**: Prevent concurrent modification conflicts using version-based locking
- **Inheritance Strategies**: Demonstrates both SINGLE_TABLE and JOINED inheritance patterns

## Technology Stack

- **Java 17**
- **Jakarta Persistence (JPA) 3.0**
- **Hibernate 6.4.4** - ORM implementation
- **PostgreSQL 16** - Relational database
- **Docker & Docker Compose** - Containerization
- **Maven 3.9+** - Build tool

## Architecture

The project follows **Clean Architecture** and **Domain-Driven Design (DDD)** principles:

```
├── domain/                    # Business logic layer
│   ├── model/                # Domain entities (immutable)
│   └── repository/           # Repository interfaces
└── infrastructure/           # Technical implementation
    └── database/
        ├── entity/           # JPA entities
        ├── mapper/           # Domain ↔ Entity converters
        ├── repository/       # Low-level JPA operations
        └── adapter/          # Repository implementations
```

### Key Design Patterns

- **Repository Pattern**: Abstract data access through interfaces
- **Adapter Pattern**: Convert between domain and persistence models
- **Inheritance Mapping**:
  - SINGLE_TABLE for Player hierarchy (performance-optimized)
  - JOINED for Tournament hierarchy (normalized storage)

## Database Schema

### Core Tables

- `teams` - Tournament teams with regions
- `players` - Players with SINGLE_TABLE inheritance (PRO/AMATEUR)
- `tournaments` - Tournaments with JOINED inheritance (ONLINE/LAN)
- `matches` - Match records with scores and statuses
- `groups` - Tournament group stages
- `team_players` - Many-to-Many relationship (Team ↔ Player)
- `tournament_registrations` - Many-to-Many relationship (Tournament ↔ Team)

### Inheritance Strategies

**SINGLE_TABLE (Player)**:
```sql
CREATE TABLE players (
    id BIGINT PRIMARY KEY,
    player_type VARCHAR(31) NOT NULL,  -- Discriminator: 'PRO' or 'AMATEUR'
    nickname VARCHAR(255) UNIQUE,
    role VARCHAR(255),
    -- PRO-specific fields
    salary DECIMAL(10,2),
    sponsor VARCHAR(100),
    -- AMATEUR-specific fields
    rank VARCHAR(50),
    hours_played INTEGER,
    version BIGINT  -- Optimistic locking
);
```

**JOINED (Tournament)**:
```sql
-- Base table
CREATE TABLE tournaments (
    id BIGINT PRIMARY KEY,
    tournament_type VARCHAR(31),
    name VARCHAR(255),
    ...
);

-- Subclass tables
CREATE TABLE online_tournaments (
    id BIGINT PRIMARY KEY REFERENCES tournaments(id),
    stream_url VARCHAR(255),
    server_location VARCHAR(100)
);

CREATE TABLE lan_tournaments (
    id BIGINT PRIMARY KEY REFERENCES tournaments(id),
    venue VARCHAR(255),
    city VARCHAR(100)
);
```

## Getting Started

### Prerequisites

- Docker Desktop (for Windows/Mac) or Docker + Docker Compose (for Linux)
- Java JDK 17 or higher
- Maven 3.9 or higher
- Git

### Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd EsportsTournamentManager
   ```

2. **Start PostgreSQL database**:
   ```bash
   cd Docker
   docker-compose up -d
   ```

   This will:
   - Start PostgreSQL 16 on port **5433** (not 5432 to avoid conflicts)
   - Create database `esportsdb` with user `esports`
   - Initialize schema and seed data via `init.sql`
   - Configure trust authentication for development

3. **Build the project**:
   ```bash
   mvn clean package
   ```

4. **Run the application**:
   ```bash
   java -jar target/EsportsTournamentManager-1.0-SNAPSHOT.jar
   ```

### Configuration

Database connection settings are in `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:postgresql://localhost:5433/esportsdb"/>
<property name="jakarta.persistence.jdbc.user" value="esports"/>
<property name="jakarta.persistence.jdbc.password" value="esportspassword"/>
<property name="hibernate.connection.isolation" value="2"/> <!-- READ_COMMITTED -->
```

## Project Structure

```
EsportsTournamentManager/
├── src/main/java/com/esports/
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Player.java (abstract)
│   │   │   ├── ProPlayer.java
│   │   │   ├── AmateurPlayer.java
│   │   │   ├── Team.java
│   │   │   ├── Tournament.java (abstract)
│   │   │   ├── OnlineTournament.java
│   │   │   ├── LanTournament.java
│   │   │   ├── Match.java
│   │   │   └── Group.java
│   │   └── repository/
│   │       ├── TeamRepository.java
│   │       ├── PlayerRepository.java
│   │       └── TournamentRepository.java
│   ├── infrastructure/database/
│   │   ├── entity/          # JPA entities
│   │   ├── mapper/          # Domain-Entity mappers
│   │   ├── repository/      # Low-level persistence
│   │   └── adapter/         # Repository implementations
│   └── Main.java
├── src/main/resources/
│   └── META-INF/persistence.xml
├── Docker/
│   ├── docker-compose.yml
│   └── init.sql
└── pom.xml
```

## Key Features Demonstration

### 1. Polymorphism & Inheritance

```java
// Domain model uses polymorphism
Player player1 = new ProPlayer(null, "s1mple", "AWPer",
                               new BigDecimal("50000"), "HyperX");
Player player2 = new AmateurPlayer(null, "rookie", "Rifler",
                                   "Global Elite", 5000);

// Both stored in single players table with discriminator
```

### 2. Many-to-Many Relationships

```java
Team team = new Team(null, "Natus Vincere", "EU");
team.addPlayer(proPlayer1);
team.addPlayer(proPlayer2);
team.addPlayer(proPlayer3);

// Validates business rules (min 3, max 7 players)
boolean eligible = team.isEligibleForTournament();
```

### 3. Transaction Management

```java
EntityTransaction tx = entityManager.getTransaction();
try {
    tx.begin();  // Start ACID transaction

    teamRepository.save(team);

    tx.commit(); // All or nothing
} catch (Exception e) {
    if (tx.isActive()) {
        tx.rollback(); // Automatic rollback on error
    }
    throw new RuntimeException("Transaction failed", e);
}
```

### 4. Optimistic Locking

```java
@Entity
public class TeamEnt {
    @Version  // Automatic conflict detection
    private Long version;

    // If two users modify the same team simultaneously,
    // the second commit will throw OptimisticLockException
}
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TeamRepositoryTest

# Skip tests during build
mvn clean package -DskipTests
```

## Database Management

### Access PostgreSQL CLI

```bash
docker-compose exec db psql -U esports -d esportsdb
```

### Useful SQL Queries

```sql
-- View all teams
SELECT * FROM teams;

-- View players by type
SELECT player_type, COUNT(*) FROM players GROUP BY player_type;

-- View tournaments with their types
SELECT t.name, t.tournament_type,
       COALESCE(o.stream_url, l.venue) as details
FROM tournaments t
LEFT JOIN online_tournaments o ON t.id = o.id
LEFT JOIN lan_tournaments l ON t.id = l.id;

-- View team rosters
SELECT t.name as team, p.nickname, p.player_type
FROM teams t
JOIN team_players tp ON t.id = tp.team_id
JOIN players p ON tp.player_id = p.id
ORDER BY t.name, p.nickname;
```

### Reset Database

```bash
# Stop and remove all data
cd Docker
docker-compose down -v

# Start fresh
docker-compose up -d
```

## Troubleshooting

### Port 5432 Already in Use

If you have PostgreSQL installed locally, it may use port 5432. This project uses port **5433** to avoid conflicts.

### Connection Refused

Make sure Docker containers are running:
```bash
docker-compose ps
```

Expected output:
```
NAME          STATUS         PORTS
docker-db-1   Up (healthy)   0.0.0.0:5433->5432/tcp
```

### Build Failures

Ensure you have Java 17+ and Maven 3.9+:
```bash
java -version
mvn -version
```

## Future Enhancements

- RESTful API with JAX-RS
- WebSocket support for live match updates
- Tournament bracket generation
- Player statistics and rankings
- Integration with streaming platforms
- Administrative web interface

## License

This project is developed for educational purposes as part of the "Non-Relational Databases" university course.

## Contact

**Nazar Kapustynskyi**

---

*Built with Clean Architecture and Domain-Driven Design principles*
