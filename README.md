# PostmanClone

PostmanClone is a lightweight, browser-based API testing tool built using Spring Boot with WebFlux, Thymeleaf, and MongoDB. It allows users to send HTTP requests (GET, POST, PUT, DELETE), manage request history, and view responses with rich metadata like headers, status, and response time.

## Features

- User login with session management
- Send HTTP requests with configurable:
    - Method (GET, POST, PUT, DELETE)
    - Headers
    - Request Body
- Real-time response rendering
    - Response body
    - Status code
    - Response headers
    - Time taken
- Request history stored per user
    - Sorted by latest
- Dracula-themed UI for a sleek developer experience

## Tech Stack

- **Backend:** Kotlin, Spring Boot, WebFlux
- **Frontend:** Thymeleaf
- **Database:** MongoDB
- **Build Tool:** Gradle
- **Testing:** JUnit 5, Mockito
- **Design Theme:** Dracula (custom CSS)

## Future Improvements

- JSON pretty print
- Separation of concern
- Error Handling
