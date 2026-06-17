# SmartPizzaAI 🍕

SmartPizzaAI is a full-stack, microservices-based pizza ordering and delivery management system. The project simulates a real-world pizza ordering platform with role-based access for **Customers**, **Admins**, and **Delivery Partners**. It includes menu browsing, cart management, order placement, AI-style recommendations, payment processing, delivery tracking, admin analytics, and service-level logging.

---

## 📌 Project Overview

SmartPizzaAI demonstrates how a modern food-ordering application can be built using a **React frontend** and **Spring Boot microservices backend**. The system uses **API Gateway** as a single entry point, **Eureka Server** for service discovery, **OpenFeign** for inter-service communication, and **MySQL** for persistent storage.

### Main Goals

- Provide a smooth pizza ordering experience for customers.
- Support role-based login for Customer, Admin, and Delivery Partner.
- Generate AI-style recommendations based on user order history.
- Allow admins to view analytics and manage pizzas.
- Automatically assign delivery partners and track delivery status.
- Maintain service-specific logs for debugging and observability.

---

## 🚀 Features

### Customer Features

- Register and login as a customer.
- Browse pizzas and categories.
- View personalized pizza recommendations.
- Add pizzas to cart.
- Increase/decrease item quantity.
- Checkout with delivery details.
- Make mock payment with GST calculation.
- View order history.
- Track active delivery status.

### Admin Features

- Login as admin.
- View dashboard analytics.
- View total orders and revenue.
- View GST collected.
- View top-selling pizzas.
- View category-wise revenue.
- Edit or delete pizzas.
- Monitor orders and delivery status.

### Delivery Partner Features

- Register/login as delivery partner.
- Load assigned delivery automatically.
- Update delivery status in controlled order:
  - `ASSIGNED`
  - `PICKED_UP`
  - `ON_THE_WAY`
  - `DELIVERED`
- Become available after delivery completion.

---

## 🧩 Microservices Used

| Service | Responsibility |
|---|---|
| Auth Service | User registration, login, JWT generation, role handling |
| Menu Service | Pizza categories, pizza details, availability, admin CRUD |
| Order Service | Cart, checkout, orders, coupon validation, delivery assignment, analytics |
| Payment Service | Mock payment, GST calculation, payment status, invoice generation |
| Recommendation Service | Top ordered pizza recommendations based on user history |
| API Gateway | Centralized routing and JWT validation |
| Eureka Server | Service registry and discovery |

---

## 🛠️ Technology Stack

### Frontend

- React
- Bootstrap
- Axios
- Formik
- Yup
- React Router

### Backend

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- Spring Cloud Gateway
- Netflix Eureka Server
- OpenFeign
- Resilience4j
- Swagger/OpenAPI

### Database

- MySQL

### Testing and Tools

- JUnit
- Mockito
- Swagger UI
- Postman
- Maven

---

## 🏗️ High-Level Architecture

```text
User
  |
  v
React Frontend
  |
  v
API Gateway
  |
  v
Eureka Service Discovery
  |
  +--> Auth Service ---------> Auth DB
  +--> Menu Service ---------> Menu DB
  +--> Order Service --------> Order DB
  +--> Payment Service ------> Payment DB
  +--> Recommendation Service
```

---

## 📂 Suggested Project Structure

```text
SmartPizzaAI/
│
├── eureka-server/
├── api-gateway/
├── auth-service/
├── menu-service/
├── order-service/
├── payment-service/
├── recommendation-service/
│
└── smartpizzaai-frontend/
    ├── src/
    │   ├── api/
    │   ├── components/
    │   ├── context/
    │   ├── pages/
    │   ├── routes/
    │   └── styles/
    └── package.json
```

---

## 🗄️ Main Database Entities

| Entity | Purpose |
|---|---|
| User | Stores user login and role details |
| Category | Stores pizza category details |
| Pizza | Stores pizza details, price, availability |
| CartItem | Stores cart items before checkout |
| CustomerOrder | Stores order summary and status |
| OrderItem | Stores pizza items inside an order |
| DeliveryPartner | Stores delivery partner details and availability |
| DeliveryTracking | Stores assigned delivery and status progress |
| Payment | Stores payment mode, GST, and total paid amount |
| Invoice | Stores generated invoice details |

---

## 🎯 Design Patterns Used

| Pattern | Usage |
|---|---|
| Microservices Architecture | Separate services for independent business modules |
| API Gateway Pattern | Single entry point for frontend requests |
| Service Discovery Pattern | Eureka Server registers and discovers services |
| Repository Pattern | Spring Data JPA repositories abstract database operations |
| DTO Pattern | Separates API request/response objects from entities |
| Dependency Injection | Spring injects services, repositories, and clients |
| Layered Architecture | Controller, Service, Repository, Entity separation |
| Circuit Breaker Pattern | Resilience4j handles service fallback scenarios |
| Client Proxy Pattern | OpenFeign acts as declarative REST client |
| Role-Based Access Control | Restricts routes and APIs based on user roles |
| State Transition Pattern | Delivery status follows controlled progress |

---

## 🔐 Authentication and Authorization

SmartPizzaAI uses JWT-based authentication.

### Roles

- `CUSTOMER`
- `ADMIN`
- `DELIVERY`

After login, users are redirected based on role:

| Role | Redirect Page |
|---|---|
| ADMIN | `/admin/dashboard` |
| DELIVERY | `/track` |
| CUSTOMER | `/menu` |

---

## 🤖 Recommendation Logic

The Recommendation Service provides AI-style pizza suggestions using order history.

### Logic

- If user has previous orders:
  - Fetch top ordered pizza IDs from Order Service.
  - Fetch pizza details from Menu Service.
  - Show top 4 pizzas.
- If user is new:
  - Show default available pizzas.

This approach is simple, explainable, and suitable for a capstone project.

---

## 💳 Payment and GST

The Payment Service performs mock payment processing.

### Payment Modes

- UPI
- Card
- Cash on Delivery

### Payment Features

- Calculates base amount.
- Calculates GST.
- Stores payment status.
- Generates invoice data.

---

## 🚚 Delivery Workflow

```text
Order Placed
    ↓
Delivery Partner Assigned
    ↓
ASSIGNED
    ↓
PICKED_UP
    ↓
ON_THE_WAY
    ↓
DELIVERED
```

Delivery status should be updated only in controlled order to avoid invalid delivery state changes.

---

## 📊 Admin Analytics

The Admin Dashboard can show:

- Total orders
- Total revenue
- GST collected
- Average order value
- Top 3 most sold pizzas
- Category-wise revenue
- Order status distribution
- Active/delivered/cancelled order count

---

## 🪵 Logging

Logs are configured using `application.properties`.

Example:

```properties
logging.file.name=logs/order-service.log
logging.level.com.smartpizzaai=INFO
```

### Recommended Log Files

```text
logs/order-service.log
logs/menu-service.log
logs/recommendation-service.log
```

### Logging Guidelines

- Use `INFO` for successful business operations.
- Use `WARN` for validation/business issues.
- Use `ERROR` for unexpected exceptions.
- Do not log passwords, JWT tokens, or payment credentials.

---

## ▶️ How to Run the Project

Follow this order strictly because API Gateway and service-to-service calls depend on Eureka and backend services being available.

### Prerequisites

Install the following before running the project:

- Java 17
- Maven
- Node.js and npm
- MySQL Server
- STS/Eclipse/IntelliJ IDEA or VS Code
- Postman or Swagger UI for API testing

---

### Step 1: Create MySQL Databases

Open MySQL and create the required databases according to your `application.properties` files.

Example:

```sql
CREATE DATABASE smartpizza_auth_db;
CREATE DATABASE smartpizza_menu_db;
CREATE DATABASE smartpizza_order_db;
CREATE DATABASE smartpizza_payment_db;
```

If your project uses a single database, create only one database:

```sql
CREATE DATABASE smartpizzaai_db;
```

---

### Step 2: Configure Backend Database Credentials

In each backend service, open:

```text
src/main/resources/application.properties
```

Update MySQL username and password:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

Also verify each service port and database URL:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/smartpizza_auth_db
```

---

### Step 3: Start Eureka Server First

Go to the Eureka Server folder and run:

```bash
cd eureka-server
mvn spring-boot:run
```

Eureka Dashboard usually runs at:

```text
http://localhost:8761
```

Make sure Eureka is running before starting other microservices.

---

### Step 4: Start Backend Microservices

Start each service in separate terminals or from your IDE.

Recommended order:

```text
1. auth-service
2. menu-service
3. order-service
4. payment-service
5. recommendation-service
6. api-gateway
```

Using terminal:

```bash
cd auth-service
mvn spring-boot:run
```

Repeat for each service:

```bash
cd menu-service
mvn spring-boot:run

cd order-service
mvn spring-boot:run

cd payment-service
mvn spring-boot:run

cd recommendation-service
mvn spring-boot:run

cd api-gateway
mvn spring-boot:run
```

After starting services, check Eureka Dashboard and confirm all services are registered.

---

### Step 5: Start React Frontend

Go to the frontend folder:

```bash
cd smartpizzaai-frontend
npm install
npm start
```

Frontend usually runs at:

```text
http://localhost:3000
```

---

### Step 6: Login and Test Role-Based Navigation

Use your registered users or seeded users.

Expected navigation:

| Role | Page |
|---|---|
| CUSTOMER | Menu page |
| ADMIN | Admin Dashboard |
| DELIVERY | Delivery Tracking page |

---

### Step 7: Test Main Flow

Recommended testing flow:

```text
Register/Login as Customer
    ↓
Browse Menu
    ↓
Add Pizza to Cart
    ↓
Checkout
    ↓
Make Payment
    ↓
Track Delivery
```

Admin flow:

```text
Login as Admin
    ↓
Open Admin Dashboard
    ↓
Check Revenue, GST, Top Pizzas and Category Revenue
    ↓
Manage Pizzas
```

Delivery flow:

```text
Login as Delivery Partner
    ↓
Load Assigned Delivery
    ↓
Update Status
    ↓
Mark Delivered
```

---

## 🔎 API Testing

Use Swagger UI or Postman to test APIs.

Common Swagger URL format:

```text
http://localhost:<service-port>/swagger-ui.html
```

or

```text
http://localhost:<service-port>/swagger-ui/index.html
```

Example:

```text
http://localhost:8081/swagger-ui/index.html
```

---

## ✅ Testing

Recommended testing approach:

- Unit test service-layer logic using JUnit and Mockito.
- Test API endpoints using Swagger/Postman.
- Test frontend validations using Formik/Yup.
- Test role-based protected routes.
- Test order placement and delivery status transitions.
- Test login failure messages for invalid credentials.

---

## 🧯 Common Issues and Fixes

### 1. Service Not Showing in Eureka

Check:

- Eureka Server is running.
- Service has correct Eureka URL.
- Service name is configured properly.

Example:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.application.name=AUTH-SERVICE
```

### 2. MySQL Connection Error

Check:

- MySQL is running.
- Database exists.
- Username/password are correct.
- Port is correct, usually `3306`.

### 3. Frontend API Calls Failing

Check:

- API Gateway is running.
- Frontend Axios base URL points to Gateway.
- JWT token is stored and sent correctly.
- CORS is configured if required.

### 4. Invalid Credentials Not Showing on UI

Check that frontend catch block sets UI error message:

```js
setServerError("Invalid credentials. Please check email and password.");
```

---

## 🔮 Future Enhancements

- Real GPS-based delivery tracking.
- Razorpay or Stripe integration.
- Inventory and stock management.
- Email/SMS notifications.
- Docker Compose deployment.
- Kubernetes cloud deployment.
- Advanced ML recommendation engine.
- Redis caching for frequently viewed menu items.
- Admin reports export in PDF/Excel format.

---

## 📌 Conclusion

SmartPizzaAI demonstrates a practical, modular, and scalable pizza ordering ecosystem. It combines modern full-stack development, microservices architecture, role-based access, AI-style recommendations, GST-aware payment processing, delivery tracking, admin analytics, and logging. The project is suitable for capstone presentation, viva explanation, and future production-level enhancements.
