# PostgreSQL Setup Guide

## Prerequisites
1. Install PostgreSQL on your system
2. Create a database named `quit_smoking_db`

## Database Setup

### 1. Install PostgreSQL (if not already installed)
- Windows: Download from https://www.postgresql.org/download/windows/
- Or use Docker: `docker run --name postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=quit_smoking_db -p 5432:5432 -d postgres`

### 2. Create Database
```sql
CREATE DATABASE quit_smoking_db;
```

### 3. Update Database Credentials
Edit `src/main/resources/application-postgresql.properties` and update:
- `spring.datasource.username` (default: postgres)
- `spring.datasource.password` (default: password)
- `spring.datasource.url` (if using different host/port)

## Running with PostgreSQL

### Option 1: Using Spring Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgresql
```

### Option 2: Set Active Profile
Add to `application.properties`:
```properties
spring.profiles.active=postgresql
```

### Option 3: Environment Variable
```bash
set SPRING_PROFILES_ACTIVE=postgresql
mvn spring-boot:run
```

## Example Data

The application will automatically load example data from `src/main/resources/data.sql`:

### Achievements
- First Step (1 day without smoking)
- Week Warrior (7 days without smoking)
- Month Master (30 days without smoking)
- Money Saver (100,000 VND saved)
- Big Saver (500,000 VND saved)
- Reduction Champion (10 cigarettes reduction)
- Health Hero (90 days without smoking)
- Quit Master (365 days without smoking)

### Plan Types
- Gradual Reduction (30 days)
- Cold Turkey (90 days)
- Moderate Reduction (60 days)
- Extended Plan (180 days)

## Testing the API

Once the application is running, you can test the achievements API:

### Get All Achievements
```bash
curl http://localhost:8080/api/v1/achievements
```

### Get Achievement by ID
```bash
curl http://localhost:8080/api/v1/achievements/1
```

### Create New Achievement
```bash
curl -X POST http://localhost:8080/api/v1/achievements \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Achievement",
    "iconUrl": "/icons/test.png",
    "criteria": "{\"type\": \"daysWithoutSmoking\", \"value\": 5}",
    "description": "Test achievement description"
  }'
```

## Frontend Integration

The frontend can now fetch real achievement data from the backend API endpoints. 