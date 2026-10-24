# Oxford International School — Comprehensive School Management System (CSMS)

### Architectural Blueprint & Implementation Specification

| Attribute | Value |
|---|---|
| **Institution** | Oxford International School |
| **Location** | Harpur, Raxaul, East Champaran, Bihar |
| **Authorized By** | Anil Kumar Gupt (Management) |
| **Lead Architect** | Sundram Kumar |
| **Target Scale** | 1,500 – 2,000 Students |
| **Backend Stack** | Java 21/23, Spring Boot 3.3.5, Spring Security (Stateless JWT RBAC), JPA/Hibernate |
| **Frontend Stack** | Angular 18 SPA + Embedded Web Client (HTML5/CSS3/ES6 Modern Dashboard) |
| **Databases** | MySQL 8.0 (Production) / In-Memory H2 (Dev/Testing Zero-Config) |
| **Observability** | Spring Boot Actuator, Micrometer, Prometheus, Grafana |

---

## 1. Executive Summary
Oxford International School requires an end-to-end digital ecosystem to streamline administrative, academic, financial, and communication workflows. This centralized platform replaces fragmented manual systems with high availability, micro-modular architecture, strict SOLID compliance, and real-time automated event notifications.

---

## 2. Core Architectural Principles (SOLID)

- **Single Responsibility Principle (SRP):**  
  Every domain service executes an isolated business responsibility (e.g., `FeeCalculationService` handles discount math, `WhatsAppNotificationService` handles async delivery, `IssueEscalationServiceImpl` monitors SLA breaches).
- **Open/Closed Principle (OCP):**  
  The Fee module utilizes the `ScholarshipStrategy` interface pattern (`MeritScholarshipStrategy`, `SiblingDiscountStrategy`, `EwsConcessionStrategy`, `StaffWardStrategy`), allowing new fee concessions to be plugged in without modifying existing calculation engines.
- **Liskov Substitution Principle (LSP):**  
  Base `Employee` entity is polymorphically extended by `TeachingStaff` and `NonTeachingStaff`. All polymorphic queries, payroll hooks, and service methods operate transparently across staff types.
- **Interface Segregation Principle (ISP):**  
  Domain repositories and service interfaces are cleanly segmented (`AdmissionService`, `TimetableService`, `TransportationService`, `ExamService`).
- **Dependency Inversion Principle (DIP):**  
  Controllers and orchestrators depend exclusively on high-level service interfaces, decoupling web presentation from data access.

---

## 3. Four Domains & Eleven Modules

### Domain A: Core Administration & Identity
1. **Admission Module:** Complete lifecycle from initial prospect inquiry, multi-field application submission, birth/transfer certificate verification, and 1-click conversion to enrolled student with automatic ID issuance (`OIS-2026-XXXX`).
2. **Student Module:** Centralized directory with student 360 profile, attendance monitoring, academic history, guardian details, and fee clearance tracking.
3. **Employee Module (LSP):** Complete staff lifecycle covering Teaching Faculty (specialization, weekly target periods, class teachers) and Non-Teaching Staff (operations, transport, lab support, security shifts).

### Domain B: Financial & Operational Logistics
4. **Fee Module (OCP):** Dynamic class-wise fee schedules, automated invoice generation, extensible scholarship engine (Merit 25%, Sibling 15%, EWS 50%, Staff Ward 75%), payment recording (UPI, Cash, Card, NetBanking), and instant printable receipts.
5. **Leave Management:** Multi-tier workflow for faculty and student absence requests with approval/rejection audit tracking.
6. **Transportation Module:** Fleet management (Tata Marcopolo buses, Force Travellers), driver registry, route stop timeline mapping (Raxaul Railway Station &bull; Customs Chowk &bull; Harpur Campus), and student transit fee links.

### Domain C: Academic Execution
7. **Auto Timetable Creator:** Algorithmic scheduling engine solving resource conflicts across teachers, classrooms, and subject weekly distribution targets (Monday–Saturday, Periods 1–7).
8. **Activity Plans:** Extracurricular management for Robotics & AI Club, Oxford Debate Society, Eco & Science Explorers, and upcoming school-wide event calendars.
9. **Exam and Reports:** Term-wise gradebook, marks entry grid, percentage calculation, letter grades (A1 to E), teacher remarks, and instant printable official Oxford International School report cards.

### Domain D: Communication & Support
10. **WhatsApp Alert System:** Event-driven asynchronous messaging decoupling dispatches from core request threads with instant alerts for pending fee dues, unexcused student absences, and management broadcasts.
11. **Issue Escalation Module:** SLA-tracked ticketing system for facilities, academic grievances, and IT issues. Automatically detects deadline breaches and escalates tickets directly to **Anil Kumar Gupt (Management)**.

---

## 4. Role Segregation & User Authentication

The application enforces strict **Individual User-Level Privilege Segregation** powered by stateless Spring Security JWT tokens:

### 🎓 1. Student Portal (Self-Service Privilege Only)
- **Restricted Access:** Students have access strictly to their personal records. They cannot view or manage other students, fee settings, or faculty data.
- **Modules Available:**
  - **My Profile & Academic Record:** Personal bio, enrollment details, parent contacts, attendance percentage (`94.2%`).
  - **My Fee Invoices & Receipts:** Personal fee dues, tuition records, and printable payment receipts.
  - **My Class Timetable:** Live schedule for Monday–Saturday for their assigned section.
  - **My Bus Route & Stop:** Transit bus allocation (`Bus #1 - Tata Marcopolo`), pickup stop (`Raxaul Railway Station`), and timings.
  - **Clubs & Activities:** Enrollment in clubs (Robotics Club, Debate Society) and event schedules.
  - **Apply Leave / Absence Note:** Submit absence requests directly to class teacher.
  - **Submit Helpdesk Ticket:** Raise maintenance or academic questions with SLA tracking.

### 👩‍🏫 2. Teacher Portal (Own Teaching Privilege + Reportees Management)
- **Role Scope:** Educators manage their own schedule and workload, plus full control over their assigned reportees (e.g., Class 10A).
- **Modules Available:**
  - **Faculty Teaching Schedule:** Assigned weekly periods, subjects, and classroom venues.
  - **My Reportees Roster (Class 10A):** Direct access to manage the 40+ students in their assigned section.
  - **Student Attendance Marker:** Daily roll call and status tracking for reportees.
  - **Marks Entry & Gradebook:** Enter Mid-Term and Annual marks for Class 10A students across English, Mathematics, Science, and Social Science.
  - **Generate Report Cards:** Review and print verified official Oxford report cards for section students.
  - **Absentee WhatsApp Alert:** 1-click trigger to notify parents of absent reportees via WhatsApp.
  - **Staff Leave Application:** Apply for faculty casual/earned leaves.

### 🛡️ 3. Admin / Management Portal (Super User with Institutional Access)
- **Role Scope:** Complete administrative oversight across the entire school ecosystem.
- **Modules Available (All 11 Modules across 4 Domains):**
  - **Core Administration:** Admission inquiries & 1-click enrollment, master Student 360 directory, full Employee & Payroll lifecycle.
  - **Finance & Operations:** Class fee structures, scholarship concession rules, payment collection, fleet vehicle & driver tracking, all staff & student leave approvals.
  - **Academic Execution:** Algorithmic Auto Timetable conflict-free creator, school-wide club and event calendar, comprehensive gradebook & marks audits.
  - **Communication & Support:** Institutional WhatsApp broadcast console (fee reminders, weather alerts), Issue Escalation dashboard with automated escalation to **Anil Kumar Gupt (Management)**.

---

## 5. Default Credentials & Role Quick-Fill

| Username | Password | Role | User Type | Access Scope |
|---|---|---|---|---|
| `admin` | `Admin@123` | `ROLE_ADMIN`, `ROLE_MANAGEMENT` | `ADMIN` | Super User (All 11 Modules) |
| `anil.gupt` | `Anil@123` | `ROLE_MANAGEMENT` | `ADMIN` | Management Escalation Head |
| `teacher.sharma` | `Teacher@123` | `ROLE_TEACHER` | `TEACHER` | Class 10A Lead + Reportees Management |
| `student.aarav` | `Student@123` | `ROLE_STUDENT` | `STUDENT` | Individual Student Self-Service |

---

## 6. Running the Application

### Option A: Operational Modes via `start.bat` (Windows)
The [`start.bat`](file:///C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/start.bat) script supports CLI lifecycle modes:

```cmd
# 1. Start application (Default / No args)
.\start.bat

# 2. Stop running application (Frees Port 8080)
.\start.bat stop

# 3. Restart application
.\start.bat restart

# 4. Check application runtime status
.\start.bat status
```

### Option B: Local Run via Maven
The backend includes the fully compiled, zero-config embedded SPA:
```bash
cd backend
mvn spring-boot:run
```
Once started, access the complete portal in your browser at:
**`http://localhost:8080/`**

API documentation and telemetry:
- **Actuator Health:** `http://localhost:8080/actuator/health`
- **Prometheus Metrics:** `http://localhost:8080/actuator/prometheus`
- **H2 Console:** `http://localhost:8080/h2-console` (`jdbc:h2:mem:oxford_csms_db`)

### Option B: Containerized Deployment (Docker Compose)
To run the full stack with MySQL 8, Spring Boot backend, Angular frontend, Prometheus, and Grafana:
```bash
docker-compose up --build
```
Ports:
- **Frontend SPA:** `http://localhost:4200`
- **Backend API:** `http://localhost:8080`
- **Prometheus:** `http://localhost:9090`
- **Grafana:** `http://localhost:3000` (User: `admin` / Password: `admin`)
