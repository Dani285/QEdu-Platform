QEdu Online Learning platform.
Frontend: Next.js + React, Frontend runtime: Node.js, Backend: Java Spring Boot, API: RESTAPI, Database: PostgreSQL.
Main modules Backend, Timetable, Statistics, Database, Login, Session handling, Roles, School events, Canteen,Frontend, UI, Tests, Projects, Study materials, Meal plan, Data input, Student evaluation, Messages.

License:MIT

AUTH

POST   /api/auth/login

POST   /api/auth/register

POST   /api/auth/logout

GET    /api/auth/me

USERS

POST   /api/users

GET    /api/users

GET    /api/users/{id}

PATCH  /api/users/{id}

DELETE /api/users/{id}

PROJECTS

GET    /api/projects

POST   /api/projects

PUT    /api/projects/{id}

DELETE /api/projects/{id}

EXAMS

GET    /api/exams

POST   /api/exams

PUT    /api/exams/{id}

DELETE /api/exams/{id}

TIMETABLE

GET    /api/timetable

POST   /api/timetable

EVENTS

GET    /api/events

POST   /api/events

DELETE /api/events/{id}

ATTENDANCE

GET    /api/attendance

POST   /api/attendance

PUT    /api/attendance/{id}

DELETE /api/attendance/{id}

STATISTICS

GET    /api/statistics

CANTEEN

GET    /api/canteen

POST   /api/canteen

DELETE /api/canteen/{id}

STUDENTS

GET    /api/students

MATERIALS

GET    /api/materials

POST   /api/materials

PUT    /api/materials/{id}

DELETE /api/materials/{id}

CLASS SUBJECTS

GET    /api/class-subjects

POST   /api/class-subjects

PUT    /api/class-subjects/{id}

DELETE /api/class-subjects/{id}

GET    /api/class-subjects/{id} 

PUT    /api/class-subjects/{id}

GRADES

GET    /api/grades

POST   /api/grades

PATCH  /api/grades/{id}

DELETE /api/grades/{id}

MESSAGES

GET    /api/messages/threads

POST   /api/messages/threads

PUT    /api/messages/threads/{id}

DELETE /api/messages/threads/{id}

Example:
http://localhost:8080/api/timetable
http://localhost:8080/api/events
http://localhost:8080/api/canteen

mvn spring-boot:run

http://localhost:8080

http://localhost:8080/api

http://localhost:8080/api/auth/login

