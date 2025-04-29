# Car Rental Application Architecture Explanation

## Overview

This car rental desktop application is built in Java using JavaFX for the user interface. The application allows users to manage car rentals, clients, contracts, payments, and vehicles. It follows industry-standard architectural patterns that make the code organized, maintainable, and scalable:

1. **MVC (Model-View-Controller)** pattern for separation of concerns
2. **DAO (Data Access Object)** pattern for database operations

## Application Structure

From the screenshot, we can see that the application is organized into several packages:

### 1. Main Package (com.rental.app)
- Contains the main application class that initializes and launches the JavaFX application
- Sets up the primary stage (window) and configures the application scene

### 2. Config Package
- **DatabaseConfig**: Handles database connection settings and provides methods to connect to the database

### 3. Controllers Package
- Controllers act as intermediaries between the models (data) and views (UI)
- Each controller is responsible for a specific area of functionality:
  - **ClientController**: Handles operations related to client management (add, update, delete, search clients)
  - **ContratController**: Manages rental contracts between clients and vehicles
  - **LoginController**: Handles user authentication and login processes
  - **MainController**: Controls the main application window and navigation
  - **PaiementController**: Manages payment operations and records
  - **VoitureController**: Handles vehicle management operations

### 4. DAO (Data Access Object) Package
- DAO classes provide an abstraction layer for database operations
- Each DAO corresponds to a specific entity in the application:
  - **ClientDAO**: Handles database operations for client records
  - **ContratDAO**: Manages contract data in the database
  - **PaiementDAO**: Handles payment records in the database
  - **UserDAO**: Manages user account data for system access
  - **VoitureDAO**: Handles vehicle data storage and retrieval

### 5. Models Package
- Model classes represent the business entities in the application:
  - **Client**: Represents a customer who rents vehicles
  - **Contrat**: Represents a rental agreement between a client and a vehicle
  - **Paiement**: Represents payment transactions associated with contracts
  - **User**: Represents system users who can log in and use the application
  - **Voiture**: Represents vehicles available for rent

### 6. Utils Package
- **PassewordUtil**: Handles password encryption/verification
- **SessionManager**: Manages user sessions and application state
- **ValidationUtil**: Provides validation functions for input data

## How the Application Works

### MVC Pattern Explained
The application follows the Model-View-Controller (MVC) pattern:

1. **Models** (in the models package):
   - Represent the data and business logic
   - Each model class (Client, Contrat, etc.) contains properties and methods related to that entity
   - Models don't know about views or controllers

2. **Views** (JavaFX FXML files, not visible in the screenshot):
   - Define the user interface
   - Display data to the user
   - Capture user input
   - Don't contain business logic

3. **Controllers** (in the controllers package):
   - Receive user input from views
   - Process requests, working with models and DAOs
   - Update views with results
   - Act as the "glue" between models and views

### DAO Pattern Explained
The Data Access Object (DAO) pattern separates data access logic from business logic:

1. **Purpose**:
   - Abstracts and encapsulates database access
   - Provides a clean API for data operations
   - Makes it easier to change the database without changing business logic

2. **How it works**:
   - Each entity has its own DAO (ClientDAO, VoitureDAO, etc.)
   - DAOs contain methods for CRUD operations (Create, Read, Update, Delete)
   - Controllers call DAO methods to perform database operations
   - This separation makes the code more maintainable and testable

### Application Flow Example

When a user wants to rent a car, the application flow would be:

1. User interacts with the UI (view) to create a new rental contract
2. The ContratController receives this request
3. The controller uses ClientDAO and VoitureDAO to verify client and vehicle availability
4. The controller creates a new Contrat model object with the rental details
5. The controller uses ContratDAO to save the contract to the database
6. The controller updates the view to show the result to the user
7. If a payment is made, PaiementController and PaiementDAO handle recording the payment

This structured approach makes the application:
- Easier to maintain (changes in one area don't affect others)
- More scalable (new features can be added with minimal changes to existing code)
- More testable (components can be tested in isolation)

