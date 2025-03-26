# Asset Management System

An OOAD miniproject for managing financial assets like stocks, mutual funds, and real estate investments.

## Features

- User authentication and registration
- Portfolio management
- Multiple asset types (Stocks, Mutual Funds, Real Estate)
- Dashboard with portfolio performance visualization
- Asset allocation overview
- Returns tracking and calculation
- Activity monitoring

## Technologies Used

- Java 11
- Spring Boot 2.7.10
- Spring Data JPA
- Spring Security
- JavaFX 17
- H2 Database (for development)

## Architecture Overview

The application follows the MVC (Model-View-Controller) architecture pattern:

- **Model**: JPA entities for User, Portfolio, Asset, etc.
- **View**: JavaFX FXML files providing the UI
- **Controller**: Spring services and JavaFX controllers handling business logic

The application also leverages several design patterns:
- **Factory Pattern**: For creating different asset types
- **Repository Pattern**: For data access
- **Observer Pattern**: For notifying UI of data changes
- **Strategy Pattern**: For different investment strategies

## Setup & Running

### Prerequisites

- JDK 11 or higher
- Maven 3.6 or higher

### Build & Run

1. Clone the repository or download the source code
2. Navigate to the project directory in your terminal
3. Build the project with Maven:

```
mvn clean package
```

4. Run the application:

```
mvn javafx:run
```

Alternatively, you can run the executable JAR file:

```
java -jar target/asset-management-0.0.1-SNAPSHOT.jar
```

## Default Login

For testing purposes, a default administrator account is provided:
- Username: `admin`
- Password: `admin`

## Development Setup

If you want to import the project into an IDE:

1. Import as Maven project
2. Ensure your IDE supports JavaFX (may require additional plugins)
3. Set up Java 11 JDK
4. Run the `AssetManagementJavaFXApplication` as the main class

## Screenshots

(Screenshots will be added here)

## UML Diagrams

(UML diagrams will be added here)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributors

- Akshay Simha
- Anikait Nanjundappa
- Anish D B
- Ashish Chandra

## Acknowledgments

- Thanks to our professors and mentors for their guidance 
