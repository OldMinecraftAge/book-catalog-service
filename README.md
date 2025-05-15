# 📚 Book Catalog Service

![GitHub release](https://img.shields.io/github/v/release/OldMinecraftAge/book-catalog-service?style=flat-square)

Welcome to the Book Catalog Service! This is a secure backend service designed to manage books efficiently. With features like admin login and two-factor authentication, it ensures that your book data is safe and accessible only to authorized users.

## Table of Contents

1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
   - [Running the Application](#running-the-application)
4. [Usage](#usage)
   - [Admin Login](#admin-login)
   - [Two-Factor Authentication](#two-factor-authentication)
   - [CRUD Operations](#crud-operations)
5. [API Documentation](#api-documentation)
6. [Contributing](#contributing)
7. [License](#license)
8. [Contact](#contact)
9. [Releases](#releases)

## Features

- **Secure Admin Login**: Access the service with admin credentials.
- **Two-Factor Authentication**: Use an authenticator app to generate temporary access codes.
- **CRUD Operations**: Easily add, view, update, or delete book entries.
- **User-Friendly Interface**: Designed with simplicity in mind for a seamless experience.

## Technologies Used

- **Java 21**: The programming language used for backend development.
- **Spring Boot**: A framework that simplifies the setup and development of new applications.
- **Spring Security**: Provides authentication and authorization.
- **H2 Database**: A lightweight database for development and testing.
- **MySQL Database**: For production data storage.
- **Docker**: Containerization for easy deployment.
- **Google Authenticator**: For two-factor authentication.
- **CRUD API**: For managing book data.

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- Java 21
- Docker
- Maven
- MySQL (if not using H2 for testing)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/OldMinecraftAge/book-catalog-service.git
   ```

2. Navigate to the project directory:

   ```bash
   cd book-catalog-service
   ```

3. Build the project using Maven:

   ```bash
   mvn clean install
   ```

### Running the Application

To run the application, you can use Docker. Ensure Docker is running on your machine.

1. Build the Docker image:

   ```bash
   docker build -t book-catalog-service .
   ```

2. Run the Docker container:

   ```bash
   docker run -p 8080:8080 book-catalog-service
   ```

The application will now be accessible at `http://localhost:8080`.

## Usage

### Admin Login

To log in as an admin, navigate to `http://localhost:8080/login`. Enter your admin credentials. 

### Two-Factor Authentication

After logging in, you will receive a prompt to enter a temporary access code. Use an authenticator app to generate this code. 

### CRUD Operations

Once logged in, you can manage your book catalog:

- **Add a Book**: Fill out the form with book details and submit.
- **View Books**: Access a list of all books in the catalog.
- **Update a Book**: Select a book and modify its details.
- **Delete a Book**: Remove a book from the catalog.

## API Documentation

The Book Catalog Service provides a RESTful API for all operations. Here are some key endpoints:

- **GET /books**: Retrieve a list of all books.
- **POST /books**: Add a new book.
- **PUT /books/{id}**: Update an existing book.
- **DELETE /books/{id}**: Delete a book.

For more details on each endpoint, refer to the API documentation within the project.

## Contributing

We welcome contributions! If you would like to contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For questions or suggestions, feel free to reach out to the repository owner.

## Releases

To download the latest release, visit [Releases](https://github.com/OldMinecraftAge/book-catalog-service/releases). You can find the necessary files to download and execute.

Feel free to explore the releases section for previous versions and updates. If you encounter any issues, check the "Releases" section for more information.

---

Thank you for checking out the Book Catalog Service! We hope it meets your needs for managing books securely and efficiently.