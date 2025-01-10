# E-commerce Backend System

## Description
This project is a robust backend system for an e-commerce platform, designed and implemented using **Java Spring Boot**. It supports essential features like user management, product management, cart functionality, and order management. The system is optimized for **scalability**, **security**, and **performance** to provide seamless integration with frontend applications. The project incorporates best practices for backend development, including **modular architecture**, **JWT authentication**, and **RESTful API design**.

## Key Features
### 1. **Authentication and Authorization**
- Implemented **JWT-based authentication** for secure API access.
- **Role-based access control** for managing user permissions.
- **Cookie-based authentication** for enhanced user experience and security.

### 2. **User Management**
- APIs for user registration, login, and profile management.
- Password encryption using **BCrypt** for secure storage.
- Validation for email, username, and password fields.

### 3. **Product Module**
- APIs for CRUD operations on products.
- Search functionality to find products by **keywords** or **categories**.
- Pagination and sorting for product listings.

### 4. **Category Module**
- APIs for creating, updating, and deleting product categories.
- Search functionality by category name or ID.

### 5. **Cart Functionality**
- Each user has a single cart associated with their account.
- APIs for adding, updating, and removing products in the cart.
- Dynamic calculation of total price and discounts.

### 6. **Order Management**
- Converts cart into an order upon checkout.
- Detailed order history for users.
- Supports multiple payment methods and payment tracking.

### 7. **Payment Module**
- Tracks payment details, including amount, payment ID, and payment gateway.

### 8. **Error Handling and Validation**
- Custom **global exception handler** for efficient error management.
- Extensive validation on all fields for data integrity.

## Tech Stack
- **Java**
- **Spring Boot** (with Spring Security)
- **MySQL** (production database)
- **H2 Database** (testing environment)
- **Postman** (API testing)
- **Maven** (project management and dependencies)

## Project Architecture
- **Controller Layer**: Handles incoming HTTP requests.
- **Service Layer**: Contains business logic.
- **Repository Layer**: Interfaces with the database.
- **Model Layer**: Defines database entities.

## Key Highlights
- **RESTful APIs**: Designed for seamless integration with frontend applications.
- **Pagination and Sorting**: Ensures efficient data handling.
- **Modular Design**: Clean separation of concerns for maintainability and scalability.
- **Security**: Implements **Spring Security** for authentication and authorization.
- **Testing**: Comprehensive API testing using Postman.

## Installation and Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/shobhit1905/E_Commerce.git
   ```
2. Navigate to the project directory:
   ```bash
   cd E_Commerce
   ```
3. Install dependencies:
   ```bash
   mvn install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Project Entities and Relationships
- **User**: Manages user accounts and authentication.
- **Product**: Stores product details.
- **Category**: Groups products into categories.
- **Cart**: Links users to their selected products.
- **Order**: Tracks completed transactions.
- **Payment**: Tracks payment details and status.

## Future Enhancements
- Integration with third-party payment gateways.
- Real-time notifications for order updates.
- Adding support for microservices architecture.
- Developing and integration with a full responsive frontend application.

## Keywords for Recruiters
- **Backend Development**
- **Java Spring Boot**
- **RESTful APIs**
- **JWT Authentication**
- **MySQL**
- **Postman Testing**
- **H2 Database**
- **Scalability**
- **Security**
- **Modular Architecture**
- **E-commerce System**
- **Spring Security**
- **Pagination and Sorting**

---
### Author
**Shobhit Nautiyal**  
Backend Developer | Passionate about scalable and secure systems  
GitHub: [shobhit1905](https://github.com/shobhit1905)

