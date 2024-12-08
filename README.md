# E-commerce Backend Application

## Description
This project is a backend application for an e-commerce platform built with Spring Boot. It provides RESTful APIs for managing items, carts, orders, and discount codes.

## Features
- Item management (CRUD operations)
- Shopping cart functionality
- Order processing
- Discount code generation and application
- Admin statistics

## Technologies Used
- Java 11
- Spring Boot 2.5.x
- Spring Data JPA
- H2 Database (for development)
- Maven
- JUnit 5 for testing

## Getting Started

### Prerequisites
- JDK 11 or later
- Maven 3.6 or later

### Installation
1. Clone the repository:
   ```git clone https://github.com/yourusername/ecommerce-backend.git```
2. Navigate to the project directory:
   ```cd ecommerce-backend```
3. Build the project:
   ```mvn clean install```

### Running the Application
Run the application using Maven:
   ```mvn spring-boot:run```

The application will start running at `http://localhost:8080`.

## API Endpoints

### Items
- GET `/api/items` - Get all items
- GET `/api/items/{id}` - Get a specific item
- POST `/api/items` - Create a new item
- PUT `/api/items/{id}` - Update an item
- DELETE `/api/items/{id}` - Delete an item

### Carts
- POST `/api/carts` - Create a new cart
- GET `/api/carts/{id}` - Get a specific cart
- POST `/api/carts/{id}/items` - Add an item to a cart
- DELETE `/api/carts/{id}/items` - Remove an item from a cart

### Orders
- POST `/api/orders` - Create a new order
- GET `/api/orders/{id}` - Get a specific order
- POST `/api/orders/checkout` - Checkout process

### Admin
- GET `/api/admin/stats` - Get admin statistics
- POST `/api/admin/generate-discount` - Generate a new discount code

## Running Tests
To run the tests, execute:
   ```mvn test```

## Configuration
The application uses `application.properties` for configuration. You can modify database settings, server port, etc., in this file.

