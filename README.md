QEdu Online Learning platform
Framework Frontend: Next.js + React, Frontend runtime: Node.js, Backend: Java Spring Boot, API: REST, Database: PostgreSQL.
Main modules Backend, Timetable, Statistics, Database, Login, Session handling, Roles, School events, Canteen,Frontend, UI, Tests, Projects, Study materials, Meal plan, Data input, Student evaluation, Messages.

License:MIT

AUTH
POST   /api/auth/login
POST /api/auth/register
POST   /api/auth/refresh
POST   /api/auth/logout
GET    /api/auth/me

USERS - ADMIN only
POST   /api/users
GET    /api/users
GET    /api/users/{id}
PATCH  /api/users/{id}/disable

TIMETABLE
GET    /api/timetable
GET    /api/timetable/day/MONDAY
POST   /api/timetable           ADMIN, TEACHER
PUT    /api/timetable/{id}      ADMIN, TEACHER
DELETE /api/timetable/{id}      ADMIN

EVENTS
GET    /api/events
GET    /api/events/upcoming
POST   /api/events              ADMIN, TEACHER
PUT    /api/events/{id}         ADMIN, TEACHER
DELETE /api/events/{id}         ADMIN

STATISTICS
GET    /api/statistics/dashboard

CANTEEN
GET /api/auth/canteen
POST /api/canteen

Example:
http://localhost:8080/api/timetable
http://localhost:8080/api/events
http://localhost:8080/api/canteen


mvn spring-boot:run

http://localhost:5432

http://localhost:5432/api

