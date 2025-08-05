Cinema Booking Application
Project Overview

This is a console-based (CLI) Java application designed to simulate a basic cinema ticket booking system. It allows users to register, log in as either a customer or a manager, view movie showtimes, book seats, and manage cinema operations. The project demonstrates core Object-Oriented Programming (OOP) principles, data persistence using JDBC with SQLite, robust error handling, and a modular architectural design.

Key Features:

    User Management:

        Customer and Manager registration and login.

        Role-based access control.

    Movie Management (Manager):

        Add new movies (title, description, duration, genre).

        View existing movies.

    Showtime Management (Manager):

        Schedule new showtimes for existing movies, specifying date/time, hall, and the exact number of available seats, and price.

        Delete existing showtimes (with cascading deletion of related bookings and seats).

        Automated cleanup of past showtimes and movies without showtimes.

    Booking Functionality (Customer):

        View available showtimes.

        Select specific seats for booking.

        Make new bookings with automatic price calculation.

        View personal booking history.

    Reporting (Manager):

        Generate a report of all bookings.

    Data Persistence:

        All application data is persistently stored in an SQLite database (cinema.db).

        Database tables are initialized and populated with sample data on application startup.

    Robustness:

        Comprehensive exception handling for user input and database operations.

        Transactional operations for data integrity

        Logging of application activities and errors to files.

Setup and Execution Instructions

Follow these steps to set up and run the Cinema Booking Application on your local machine.
Prerequisites

    Java Development Kit (JDK) 21 or higher: Ensure you have a compatible JDK installed. You can download it from Oracle or AdoptOpenJDK.

    SQLite JDBC Driver: This project uses SQLite as its database. You will need the SQLite JDBC driver JAR file.

        Download the latest sqlite-jdbc-3.45.1.0 from Maven Central.

    SLF4J (Simple Logging Facade for Java): Used for logging.

        Download slf4j-api-2.0.13.jar and slf4j-simple-2.0.13.jar from Maven Central 
    An Integrated Development Environment (IDE): IntelliJ IDEA Community Edition is recommended for ease of setup.

Project Setup

    Clone the Repository:

    git clone https://github.com/AnaniyaYosef/Main_CCMS.git
    cd Main_CCMS

    Open in IntelliJ IDEA:

        Open IntelliJ IDEA.

        Select File > Open... and navigate to the cloned Main_CCMS directory.

    Configure Project SDK:

        Go to File > Project Structure... (Ctrl+Alt+Shift+S or ⌘;).

        Under Project Settings > Project, ensure your Project SDK is set to JDK 21 (or your installed compatible version).

        Set Project language level to 21.

    Add Libraries (JDBC and SLF4J JARs):

        In Project Structure (Ctrl+Alt+Shift+S or ⌘;), go to Project Settings > Libraries.

        Click the + button, select Java.

        Navigate to the directory where you downloaded sqlite-jdbc-X.X.X.jar, slf4j-api-2.0.13.jar, and slf4j-simple-2.0.13.jar. Select all three JARs and click OK.

        Ensure these libraries are added to your project's module.

    Mark src as Sources Root:

        In Project Structure, go to Project Settings > Modules.

        Select your project's module (e.g., Main_CCMS).

        Go to the Sources tab.

        Right-click on the src folder and select Mark as > Sources Root. This is crucial for db_config.txt to be found on the classpath.

    Create db_config.txt:

        Inside your src folder, create a new directory named config.

        Inside the config directory, create a new file named db_config.txt.

        Add the following line to db_config.txt:

        db.url=jdbc:sqlite:cinema.db

        This tells the application to create/connect to an SQLite database file named cinema.db in your project's root directory.

    Clean and Rebuild Project:

        Go to Build > Clean Project.

        Go to Build > Rebuild Project.

        If you encounter persistent issues, try File > Invalidate Caches / Restart..., select all options, and restart IntelliJ.

Running the Application

    Locate the main class: The entry point of the application is the main method in ui/main.java.

    Run: Right-click on the main.java file in IntelliJ and select Run 'main.main()'.

The application will start in your IDE's console, prompting you for choices between Customer and Manager roles.

Initial Credentials:

    The application will initialize with a sample customer:

        Email: mandy@example.com

        Password: password123

    You can also register new users (both customers and managers) through the main menu.

Team Contributions

This project was a collaborative effort. Below are the contributions of each team member:
Team Contributions

This project was a collaborative effort. Below are the contributions of each team member:

    Ananiya Yosef

        Developed the User Interface (ui package) including Auth, CustomerMenu, and ManagerMenu.

        Implemented utility functionalities (util package) such as DBUtil for database connection management, FileLogger for logging and reporting, and InputValidator for input validation.

        Contributed to the Data Access Object (DAO package) layer, including UserDAO, MovieDAO, ShowTimeDAO, SeatDAO, and BookingDAO.

        Managed database setup, initialization, and ensuring robust data persistence.

    Hana Adane

        Developed the Service layer (service package), including BookingService, which encapsulates core business logic and transaction management.

        Implemented abstraction principles throughout the project, particularly through the use of abstract classes and interfaces.

    Salem Berihun

        Developed the Model layer (model package), including all core data entities such as User, Customer, Manager, Movie, ShowTime, Seat, and Booking.

        Ensured proper encapsulation, inheritance, and polymorphism within the model classes.
