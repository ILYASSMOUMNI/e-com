#important 
run mvn clean install
to run project  mvn compile exec:java

add mysql db ecommerce
go to src/recources . copy paste l base donnees
# E-Commerce Swing Application

A simple e-commerce application built with Java Swing and MySQL.

## Features

- User authentication (login/register)
- Product catalog browsing
- Shopping cart functionality
- Order management

## Prerequisites

- Java 11 or higher
- Maven
- MySQL Server

## Setup

1. Clone the repository
2. Configure the database connection in `src/main/java/com/yourname/ecommerce/database/DBConnection.java`
3. Run `mvn clean install` to build the project
4. Run the application using `mvn exec:java -Dexec.mainClass="com.yourname.ecommerce.main.Main"`

## Project Structure

- `src/main/java/com/yourname/ecommerce/`
  - `main/` - Main application entry point
  - `gui/` - Swing UI components
  - `models/` - Data models
  - `services/` - Business logic
  - `database/` - Database connection and queries 


