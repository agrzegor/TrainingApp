# TrainingApp

A full-stack personal training management application that allows trainers to manage their customers, schedule training sessions, and assign exercises. Customers can view their sessions and track their training progress.

---

## Tech Stack

### Backend
- **Java 21** / **Spring Boot 4.0.5**
- **Spring Security** with JWT authentication
- **Spring Data JPA** / **Hibernate** — `JOINED` inheritance strategy
- **MySQL 8** — relational database
- **Lombok** — boilerplate reduction
- **jjwt 0.13** — JWT token generation and validation
- **NanoId** — unique trainer identifier generation
- **RapidAPI (AscendAPI)** — external exercise database with videos and images
- **Maven** — build tool

### Frontend
- **React 19** / **TypeScript** / **Vite**
- **React Router v7** — client-side routing
- **shadcn/ui** + **Radix UI** — component library
- **Tailwind CSS v4** — utility-first styling
- **Axios** — HTTP client with JWT interceptor
- **Lucide React** — icons

---

## Features

### Authentication
- Trainer and Customer registration with role selection
- JWT-based login — token stored in `localStorage`, attached to every request
- Password minimum length validation (8 characters) enforced on frontend and backend
- Protected routes — unauthenticated users redirected to login

### Trainer
- View and edit own profile (name, phone)
- Unique trainer identifier code — share with customers to link
- Browse linked customers
- Unlink a customer (automatically cancels all future sessions with that customer)
- Create, update, and delete training sessions (future sessions only)
- Overlap validation — cannot schedule two sessions in the same time window
- Add exercises to sessions (searched from external API), configure sets, reps, and weight
- Remove individual exercises from a session
- View full exercise details (overview, instructions, tips, video)

### Customer
- View and edit own profile
- Link to a trainer using their identifier code
- Unlink from trainer (automatically cancels all future sessions)
- View all assigned training sessions

### Sessions
- Tabs: **Upcoming** / **Active** (in progress right now) / **Past** / **All**
- Session cards show customer name — persisted in the DTO, remains correct even after a customer is unlinked
- Session detail page shows status badge: Upcoming / Active / Completed

---

## Project Structure

```
TrainingApp/
├── src/main/java/pl/coderslab/trainingapp/
│   ├── config/          # Security config, JWT filter, CORS
│   ├── controller/      # REST controllers (User, Trainer, Customer, Session, Exercise)
│   ├── dto/             # Request/response DTOs and API records
│   ├── entity/          # JPA entities (User, Trainer, Customer, TrainingSession, SessionExercise, Exercise)
│   ├── mappers/         # Entity → DTO mapper
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JwtService
│   └── service/         # Business logic services
├── src/main/resources/
│   └── application.properties
└── frontend/            # React + TypeScript frontend
    ├── src/
    │   ├── components/  # UI components and layout
    │   ├── context/     # AuthContext (JWT + userType)
    │   ├── lib/         # Axios instance with JWT interceptor
    │   ├── pages/       # Page components
    │   └── types/       # TypeScript interfaces
    └── vite.config.ts   # Proxy: /api → localhost:8080
```

---

## Data Model

```
User (abstract, JOINED inheritance)
├── Trainer  — has identifier, list of customers
└── Customer — belongs to one Trainer

TrainingSession  — belongs to Trainer + Customer, has list of SessionExercise
SessionExercise  — join between TrainingSession and Exercise (sets, reps, weight)
Exercise         — local record linked to external exercise ID (RapidAPI)
```

---

## API Endpoints

| Method | Path | Description | Role |
|--------|------|-------------|------|
| POST | `/api/signup` | Register new user | Public |
| POST | `/api/login` | Login, returns JWT | Public |
| GET | `/api/trainers/me` | Get own trainer profile | Trainer |
| PUT | `/api/trainers` | Update trainer profile | Trainer |
| GET | `/api/trainers/{id}` | Get trainer by ID | Trainer / assigned Customer |
| GET | `/api/trainers/me/customers` | List linked customers | Trainer |
| GET | `/api/trainers/customers/{id}` | Get customer detail | Trainer |
| DELETE | `/api/trainers/customers/{id}` | Unlink customer | Trainer |
| GET | `/api/customers/me` | Get own customer profile | Customer |
| PUT | `/api/customers` | Update customer profile | Customer |
| POST | `/api/customers/{identifier}` | Link to trainer | Customer |
| DELETE | `/api/customers/me/trainer` | Unlink from trainer (cancels future sessions) | Customer |
| GET | `/api/sessions` | List sessions | Trainer / Customer |
| POST | `/api/sessions` | Create session | Trainer |
| PUT | `/api/sessions/{id}` | Update session | Trainer |
| DELETE | `/api/sessions/{id}` | Delete future session | Trainer |
| GET | `/api/sessions/{id}/exercises` | List session exercises | Trainer / Customer |
| POST | `/api/sessions/{id}/exercises` | Add exercise to session | Trainer |
| DELETE | `/api/sessions/{id}/exercises/{exerciseId}` | Remove exercise from session | Trainer |
| GET | `/api/exercises/search?search=` | Search exercises (external API) | Trainer |
| GET | `/api/exercises/{id}` | Get exercise details | Trainer / Customer |
| DELETE | `/api/exercises/{id}` | Delete exercise from library | Trainer |

---

## Getting Started

### Prerequisites
- Java 21
- Maven 3.9+
- MySQL 8
- Node.js 18+

### Database Setup

```sql
CREATE DATABASE gym;
```

Hibernate will auto-create and update the schema on first run (`spring.jpa.hibernate.ddl-auto=update`).

### Backend Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gym?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password

security.jwt.secret-key=your_secret_key_at_least_32_chars
security.jwt.expiration-time=3600000

api.rapid.host=edb-with-videos-and-images-by-ascendapi.p.rapidapi.com
api.rapid.url=https://edb-with-videos-and-images-by-ascendapi.p.rapidapi.com/api
api.rapid.key=your_rapidapi_key
```

### Run Backend

```bash
mvn spring-boot:run
# or build and run the JAR
mvn package -DskipTests
java -jar target/TrainingApp-0.0.1-SNAPSHOT.jar
```

Server starts on `http://localhost:8080`.

### Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts on `http://localhost:3001`. All `/api` requests are proxied to the backend.

---

## External API

Exercise data (search, details, videos, images) is provided by the **EDB with Videos and Images** API available on RapidAPI. A valid API key is required in `application.properties`. Exercises fetched through the app are cached locally in the `exercise` table to reduce repeated external API calls.
