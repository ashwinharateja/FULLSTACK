# Smart Campus Event Management System

Full-stack app with Spring Boot + MySQL backend and React + Tailwind frontend.

## Tech Stack
- Backend: Java 17, Spring Boot, Spring Web, Spring Data JPA, MySQL
- Frontend: React (Vite), Tailwind CSS, Axios, React Router, React Hot Toast, Framer Motion, Recharts, QRCode, jsPDF

## Backend Setup
1. Create/start MySQL.
2. Ensure credentials match `backend/src/main/resources/application.properties`:
   - DB: `smart_campus_events`
   - Username: `root`
   - Password: `root`
3. Run backend:
   - `cd backend`
   - `mvn spring-boot:run`

Backend runs on `http://localhost:8080`.

## Frontend Setup
1. Install dependencies:
   - `cd frontend`
   - `npm install`
2. Run frontend:
   - `npm run dev`

Frontend runs on `http://localhost:5173`.

## Default Data
- Admin user: `admin@campus.com`
- Admin password: `admin123`
- Sample students and events auto-seeded on first startup.

## Main Endpoints
- `GET /api/events`
- `POST /api/events`
- `PUT /api/events/{id}`
- `DELETE /api/events/{id}`
- `GET /api/events/{id}`
- `POST /api/register`
- `DELETE /api/register`
- `POST /api/waitlist`
- `POST /api/attendance`
- `GET /api/my-events?userId=...`
- `GET /api/my-events-dashboard?userId=...`
- `GET /api/stats`
- `POST /api/admin/login`
- `GET /api/events/{eventId}/registrations`
- `GET /api/participants?eventId=...`
- `POST /api/events/{eventId}/participants/decision`
- `POST /api/bookmark`
- `GET /api/bookmarks?userId=...`
- `POST /api/feedback`
- `GET /api/events/{eventId}/feedback`
- `POST /api/notifications`
- `GET /api/export/registrations`
- `GET /api/audit-logs`

## Enhanced Features
- Smart registration with waitlist and auto-promotion when seats free
- Student dashboard widgets (upcoming/completed/cancelled) and attendance status
- Bookmarks and event comparison panel
- Quick-view event details modal with countdown and organizer
- QR attendance token + mark attendance API + certificate PDF download
- Feedback (rating + comments) for events
- Admin analytics cards + bar/pie charts
- Participant approval/rejection, CSV export, notifications API, and audit logs

