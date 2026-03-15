# 📋 TaskFlow — Full Stack Task Management App

A full-stack **Task Management Application** (mini Jira/Trello) built with **Java Spring Boot** backend and **React** frontend.

## 🚀 Tech Stack

### Backend
- **Java 17** + **Spring Boot 3.2**
- **Spring Security** + **JWT Authentication**
- **Hibernate / JPA** (ORM)
- **MySQL** (Database)
- **Maven** (Build tool)
- REST API with proper HTTP methods (GET, POST, PUT, PATCH, DELETE)

### Frontend
- **React 18** (Vite)
- **React Router v6** (Protected routes)
- **Axios** (HTTP client with interceptors)
- **Context API** (Auth state management)
- Custom CSS (no UI library — hand-crafted dark theme)

---

## ✨ Features

- 🔐 **JWT Authentication** — Register & Login with secure token-based auth
- 📋 **Kanban Board** — 3-column board: To Do → In Progress → Done
- ➕ **Create / Edit / Delete Tasks** — Full CRUD with a modal form
- 🎯 **Priority Levels** — HIGH, MEDIUM, LOW with color-coded badges
- 👤 **Assign Tasks** — Assign tasks to any registered user
- 📅 **Due Dates** — Set deadlines; overdue tasks highlighted in red
- ⚡ **Status Advance** — One-click "Start" / "Complete" buttons on task cards
- 🔍 **Filter by Priority** — Filter the board by priority level
- 📊 **Stats Dashboard** — Live counts of total, todo, in-progress, and done tasks

---

## 🗄️ Database Schema

```
users           tasks
------          ------
id              id
name            title
email (unique)  description
password        status (TODO/IN_PROGRESS/DONE)
role            priority (LOW/MEDIUM/HIGH)
created_at      due_date
                assigned_to_id (FK → users)
                created_by_id  (FK → users)
                created_at
                updated_at
```

---

## 🛠️ Setup & Run

### Prerequisites
- Java 17+
- Maven
- MySQL 8+
- Node.js 18+

### Backend Setup

1. Create MySQL database:
```sql
CREATE DATABASE taskmanager_db;
```

2. Update `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

3. Run the backend:
```bash
cd backend
mvn spring-boot:run
```
Backend starts on **http://localhost:8080**

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```
Frontend starts on **http://localhost:5173**

---

## 📡 API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/auth/register | Register new user | ❌ |
| POST | /api/auth/login | Login & get JWT | ❌ |
| GET | /api/tasks | Get all tasks | ✅ |
| GET | /api/tasks/my | Get my tasks | ✅ |
| GET | /api/tasks/{id} | Get task by ID | ✅ |
| POST | /api/tasks | Create task | ✅ |
| PUT | /api/tasks/{id} | Update task | ✅ |
| PATCH | /api/tasks/{id}/status | Update status | ✅ |
| DELETE | /api/tasks/{id} | Delete task | ✅ |
| GET | /api/users | Get all users | ✅ |

---

## 📁 Project Structure

```
taskmanager/
├── backend/                    # Spring Boot backend
│   └── src/main/java/com/taskmanager/
│       ├── entity/             # JPA entities (User, Task)
│       ├── repository/         # Spring Data JPA repos
│       ├── dto/                # Request/Response DTOs
│       ├── service/            # Business logic
│       ├── controller/         # REST controllers
│       ├── security/           # JWT filter, UserDetailsService
│       └── config/             # SecurityConfig (CORS, auth)
│
└── frontend/                   # React frontend
    └── src/
        ├── api/                # Axios instance + interceptors
        ├── context/            # AuthContext (login/logout state)
        ├── pages/              # Login, Register, Dashboard
        └── components/         # TaskCard, TaskModal
```

---

## 👨‍💻 Author

Built as a portfolio project to demonstrate Java Full Stack development skills.
