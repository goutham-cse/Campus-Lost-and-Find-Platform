# Campus Lost & Found Web Platform (Java Spring Boot)

An AI-powered Lost and Found Web Application for University Campuses built in Java.

## Features
- **User Authentication Module**: Registration and login for students & staff using Spring Security and BCrypt password encryption.
- **Found Module with AI Recognition**: Upload an image of a found item -> AI automatically detects the category (ID Card, Laptop/Phone, Wallet, Keys, Books, etc.) and pre-fills category-specific question fields.
- **Lost Module**: Search/report lost items using the identical category-specific unique questions schema.
- **Automated Matching Engine**: Real-time fuzzy matching between lost and found entries, displaying match confidence scores (e.g., 95% Match) and connecting the users.
- **H2 Persistent File Database**: Zero configuration needed, automatically persists data to `./data/campus_db`.

## How to Run

### Prerequisites
- JDK 17 or higher
- Maven 3.8+ (or wrapper)

### Build and Launch
```bash
# Compile and package
mvn clean package -DskipTests

# Run the Spring Boot application
mvn spring-boot:run
```

Access the application in your browser at:
`http://localhost:8080`

### H2 Database Console
Access the H2 Console at `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:./data/campus_db`
- **User Name**: `sa`
- **Password**: *(leave empty)*
