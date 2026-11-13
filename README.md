# DormDesk – Smart Hostel Management & Resident Support Application

> **Tagline:** *Your Hostel. Your Voice. One Desk.*  
> **Aesthetic:** *Warm, Minimal, Elegant and Professional.*

---

## 1. Overview

**DormDesk** is a modern, production-grade Android application built with **Kotlin** and **Jetpack Compose** that bridges the communication gap between hostel residents and hostel administration (Wardens).

Instead of outdated paper registers and uncoordinated messaging groups, DormDesk centralizes complaints, feedback, official notices, maintenance tracking, and AI-powered resident assistance in one unified, calm, and uncluttered interface.

---

## 2. User Roles & Access Control

DormDesk features strict two-role separation:

### 🎓 1. Student (Resident)
- **Home Dashboard:** Quick statistics (Total, Pending, Resolved), room info, latest notice bulletin, recent complaint tickets.
- **Raise Complaint:** Multi-category issue registration with priority tags, location, detailed description, and photograph attachment.
- **Complaint Tracking:** Live 7-step visual timeline (`Submitted` → `Received` → `Assigned` → `In Progress` → `Resolution Updated` → `Resolved` → `Closed`).
- **Resolution Verification:** Residents verify repair work and provide a 1–5 star rating before ticket closure, or reopen with reason.
- **Feedback & Suggestions:** Categorized suggestions (Mess, Facilities, Cleanliness, Staff Behaviour) with optional anonymous submission and official warden replies.
- **Hostel Notice Board:** Official announcements categorized by water supply, electricity, mess, events, and maintenance with pinned notices.
- **In-App Notification Centre:** Real-time updates on status transitions, technician assignments, remarks, and notices.
- **DormDesk AI:** 24/7 smart assistant answering FAQs, checking ticket status, mess rules, and warden contact hours.

### 🛡️ 2. Warden / Administrator
- **Operational Dashboard:** Live metrics (Total, Pending, In-Progress, Resolved, Urgent tickets), status distribution bar chart, average resolution time, and student satisfaction score.
- **Complaint Management:** Search, filter (category, status, priority), assign maintenance technicians by name, update statuses, record work progress, and log resolution notes.
- **Resident Verification Safeguard:** Complaints resolved by wardens require student satisfaction confirmation before closing (or formal administrative closure justification).
- **Feedback Management:** Review student feedback, filter by category, and publish official administrative responses.
- **Notice Board Management:** Create, edit, delete, set priority, and pin critical announcements.

---

## 3. Brand Identity & Color Palette

DormDesk adheres to a distinctive, premium minimalist aesthetic inspired by high-end productivity applications:

| Token | Hex Code | Visual Application |
|---|---|---|
| **Warm Ivory** | `#F7F5F0` | Light theme background, calm and clean |
| **Espresso Brown** | `#40352F` | Primary brand accent, primary buttons, headers |
| **Muted Terracotta** | `#B87962` | Secondary accent, interactive badges, highlights |
| **Warm Taupe** | `#A89B8E` | Subtle borders, secondary icons, unselected items |
| **Soft White** | `#FFFEFC` | Card surfaces, modal sheets |
| **Deep Charcoal** | `#302D2A` | Dark theme canvas, deep typography |
| **Sage Green** | `#7D927B` | Resolved status, success indicators, commendations |

---

## 4. Pre-Configured Demo Accounts

For immediate testing, two pre-configured demo accounts are provided with 1-tap demo login buttons on the login screen:

### Student Account:
- **Email:** `student@dormdesk.app`
- **Password:** `password123`
- **Name:** Aarav Patel
- **Room:** Block B, Room 204

### Warden Account:
- **Email:** `warden@dormdesk.app`
- **Password:** `password123`
- **Name:** Dr. Rajeshwar Sharma
- **Office:** Admin Block, Room G-02

*(You can also register a brand new student account or switch roles instantly from the Profile screen!)*

---

## 5. Technology Stack & Academic Concepts (CSE226)

| Concept | Implementation Details |
|---|---|
| **Kotlin** | Idiomatic Kotlin, data classes, sealed classes, coroutines |
| **Jetpack Compose** | 100% declarative UI with composable functions, state hoisting |
| **Material 3** | Adaptive color scheme, Material 3 Typography, custom components |
| **MVVM Architecture** | Strict separation of UI (Views), Business Logic (ViewModels), and Data (Repositories) |
| **StateFlow** | Reactive, lifecycle-aware state streaming (`allComplaints`, `feedbacks`, `messages`) |
| **Repository Pattern** | Modular repositories: `ComplaintRepository`, `AuthRepository`, `FeedbackRepository`, `AnnouncementRepository`, `NotificationRepository`, `ChatRepository` |
| **Kotlin Coroutines** | Asynchronous operations and non-blocking background dispatching |
| **Navigation Compose** | Type-safe declarative navigation with bottom app bars and parameterized routes |
| **Groq AI & FastAPI** | Secure proxy architecture with Groq OpenAI-compatible Chat Completions API |
| **Local FAQ Fallback** | Instant fallback ensuring 100% chatbot availability without external API dependencies |

---

## 6. Project Structure

```
DormDesk/
├── app/
│   ├── src/main/
│   │   ├── java/com/dormdesk/app/
│   │   │   ├── data/
│   │   │   │   ├── model/           # User, Complaint, Feedback, Announcement, NotificationItem, ChatMessage
│   │   │   │   ├── remote/          # RetrofitClient, AiApiService, LocalFaqEngine, DTOs
│   │   │   │   └── repository/      # AuthRepository, ComplaintRepository, FeedbackRepository, etc.
│   │   │   ├── viewmodel/           # AuthViewModel, ComplaintViewModel, ChatbotViewModel, etc.
│   │   │   ├── ui/
│   │   │   │   ├── theme/           # Color.kt, Type.kt, Theme.kt (Warm Ivory Palette)
│   │   │   │   ├── components/      # DormDeskTopBar, DormDeskCard, StatusBadge, TimelineView, etc.
│   │   │   │   ├── auth/            # LoginScreen, RegisterScreen
│   │   │   │   ├── student/         # StudentHomeScreen
│   │   │   │   ├── admin/           # WardenHomeScreen
│   │   │   │   ├── complaints/      # RaiseComplaintScreen, ComplaintListScreen, ComplaintDetailScreen
│   │   │   │   ├── feedback/        # FeedbackScreen, WardenFeedbackScreen
│   │   │   │   ├── announcements/   # AnnouncementListScreen
│   │   │   │   ├── chatbot/         # DormDeskAiScreen
│   │   │   │   ├── notifications/   # NotificationCenterScreen
│   │   │   │   ├── profile/         # ProfileScreen
│   │   │   │   └── navigation/      # DormDeskNavGraph, StudentMainScreen, WardenMainScreen
│   │   │   ├── DormDeskApp.kt       # Application container & dependency provider
│   │   │   └── MainActivity.kt      # Clean single-activity entry point
│   │   ├── res/                     # Drawables, mipmaps, values, xml configs
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── backend/                         # FastAPI AI middleware
│   ├── app/
│   │   ├── main.py                  # Endpoints: /api/chat, /api/health, /api/faq
│   │   ├── groq_service.py          # Groq LLM integration with rate limit / timeout handling
│   │   └── faq_data.py              # Local hostel intelligence knowledge base
│   ├── requirements.txt
│   ├── .env.example
│   ├── .gitignore
│   └── README.md
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 7. How to Build & Run

### Android Application
1. Open the `DormDesk` folder in **Android Studio Ladybug / Panda / Jellyfish**.
2. Wait for Gradle sync to complete.
3. Select an Android Emulator or physical device (Android 8.0+ / API 26+).
4. Click **Run 'app'** (`Shift + F10`).
5. Tap **Student Demo** or **Warden Demo** to explore immediately!

### FastAPI Backend (Optional for Groq Generative AI)
1. Open a terminal in the `backend/` directory:
   ```bash
   cd backend
   python -m venv venv
   .\venv\Scripts\Activate.ps1
   pip install -r requirements.txt
   copy .env.example .env
   ```
2. Add your Groq API key to `.env` (`GROQ_API_KEY=your_key_here`).
3. Start the server:
   ```bash
   uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
   ```
4. The Android app will automatically connect to `http://10.0.2.2:8000` when running in the emulator, or use the local FAQ assistant if the backend is not running.
