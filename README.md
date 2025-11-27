# RestApiPractice

A Spring Boot–based REST API project focused on backend architecture, API design, authentication flow, request validation, and BDD-style integration testing using Cucumber.

---

## 🚀 Features

### ✔ REST API Development
- Layered architecture (controller → service → core model)
- CRUD operations for Employee entity
- DTO-based request/response models
- Centralized error handling & structured API responses
- Request header filtering with correlation IDs

### ✔ Authentication Workflow
- Custom username/password validation
- Login endpoint with success/error response models
- Logging of authentication attempts using Log4j2

### ✔ Validation
- JSON Schema validation for:
  - Employee creation
  - Employee update
  - Login
- Schema-based contract enforcement for incoming payloads

### ✔ API Documentation
- Auto-generated Swagger UI using SpringDoc OpenAPI
- Custom OpenAPI configuration class

### ✔ Integration Testing (BDD)
- Cucumber feature files for end-to-end API testing
- Step definitions using Spring Test & JUnit 5
- Test execution via Cucumber runner class
- Validation of schema compliance and API responses

---

## 🛠 Technologies Used

**Backend**
- Java 17  
- Spring Boot 3 (Spring Web, Spring Validation)  
- SpringDoc OpenAPI  

**Testing**
- Cucumber (BDD)  
- JUnit 5  
- Spring Test  
- Mockito (if used)  

**Utilities**
- Log4j2  
- JSON Schema Validator  
- Maven  
- Lombok  

---

## ▶️ How to Run the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/USERNAME/RestApiPractice.git
