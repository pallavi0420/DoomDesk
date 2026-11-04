# DormDesk – Smart Hostel Management & Resident Support Application

> **Tagline**: *Your Hostel. Your Voice. One Desk.*  
> **Package**: `com.dormdesk.app`  
> **Course / Academic Framework**: CSE226 – Mobile Application Development  
> **Design Philosophy**: Warm, Minimal, Elegant and Professional.

---

## 1. Overview & Commercial Vision

**DormDesk** connects hostel residents with their hostel warden through a single, elegant, and unified mobile platform. It replaces outdated paper complaint registers, chaotic messaging groups, and scattered notice boards with an audited, transparent, and AI-assisted workflow.

The platform is designed exclusively for **two user roles**:
1. **Resident Student**: Register complaints, track live 7-stage resolution progress, submit feedback, read pinned circulars, and chat with DormDesk AI.
2. **Hostel Warden / Administrator**: Review hostel complaints, coordinate maintenance staff by name, log remarks, update resolution notes, publish announcements, and review student feedback.

---

## 2. Distinctive Brand Identity & Visual Palette

DormDesk departs from standard blue tech templates, adopting an earthy, warm, and comforting aesthetic inspired by premium editorial productivity tools.

| Color Name | Hex Code | Purpose |
|---|---|---|
| **Warm Ivory** | `#F7F5F0` | Primary application canvas / background |
| **Espresso Brown** | `#40352F` | Primary brand accent, high-emphasis text, primary buttons |
| **Muted Terracotta** | `#B87962` | Secondary accent, active indicators, urgent badges |
| **Warm Taupe** | `#A89B8E` | Subtle borders, dividers, secondary tags |
| **Soft White** | `#FFFEFC` | Card surfaces, modal sheets, elevated views |
| **Deep Charcoal** | `#302D2A` | Primary body typography for maximum legibility |
| **Sage Green** | `#7D927B` | Resolution success, completed stages, positive feedback |

---

## 3. Technology Stack & Academic Concepts

Built strictly using modern, production-grade Android best practices:

- **Kotlin (v1.9.22)** & **Jetpack Compose (v1.5.8)**: Declarative, reactive UI without XML boilerplate.
- **Target SDK 35 (Android 15)**: Full Google Play Console upload compliance (`targetSdk = 35`, `compileSdk = 35`, `buildToolsVersion = 35.0.0`).
- **Edge-to-Edge Enabled**: `enableEdgeToEdge()` and WindowInsets handling across all screens.
- **Material 3 (M3)**: Dynamic color elevation, typography scales, adaptive cards, and components.
- **Navigation Compose (v2.7.7)**: Single-activity architecture (`MainActivity`), type-safe arguments, and bottom navigation.
- **MVVM Architecture**: Strict separation of concerns between Views, ViewModels, and Data Layer.
- **StateFlow & Coroutines**: Reactive unidirectional data flow (`UiState`) with structured concurrency.
- **Repository Pattern**: Pluggable data layer with rich local demo dataset and Firebase Realtime Database readiness.
- **Python FastAPI Backend**: Lightweight, secure microservice for Groq Generative AI integration.
- **Local FAQ Engine Fallback**: Zero-dependency, offline-first intent resolution guaranteeing 100% assistant uptime.

---

## 4. Key Application Features

### A. 7-Stage Complaint Lifecycle
Every complaint moves through seven transparent stages:
1. **Submitted**: Resident logs the ticket with title, category, priority, room, and optional photo.
2. **Received by Warden**: Warden acknowledges the issue in the central queue.
3. **Maintenance Assigned**: Electrician, plumber, or technician allocated by name.
4. **Work in Progress**: Physical maintenance and repairs underway on-site.
5. **Resolution Updated**: Technician logs repair summary and parts replaced.
6. **Resolved**: Warden inspects and marks work completed.
7. **Closed**: **Requires resident confirmation and 1–5 star rating** (or documented admin closure). A complaint can also be reopened by the student if the issue persists.

### B. Categories & Priority Matrix
- **Categories**: Electrical, Plumbing, Cleaning & Hygiene, Furniture, Internet / Wi-Fi, Washroom, Water Supply, Security, Mess / Food, Other.
- **Priorities**: Low, Medium, High, Urgent (with dedicated emergency alerts).

### C. Feedback & Suggestions
- Separate from maintenance tickets.
- 5 categories: Hostel Facilities, Mess and Food, Cleanliness, Staff Behaviour, General Suggestions.
- 1 to 5 star ratings with **Anonymous Submission** support.
- Warden replies directly to resident suggestions.

### D. Hostel Notice Board
- Categories: General Notices, Maintenance Updates, Water Supply, Electricity, Mess Updates, Hostel Events, Emergency.
- Pinned circulars always anchored to top.
- Unread/read status tracking.

### E. In-App Notification Center
- Automated notifications for ticket creation, stage transitions, warden remarks, resolution updates, new announcements, and feedback replies.

### F. DormDesk AI – Dual-Engine Assistant
1. **Secure Python FastAPI + Groq LLM Gateway**:
   - Communicates with Groq's high-speed inference engine (`llama-3.3-70b-versatile` or `llama-3.1-8b-instant`).
   - Secure API key handling via `.env` (never exposed to Android client).
2. **Intelligent Local FAQ Engine**:
   - Zero-API fallback when offline or when Groq API key is unconfigured.
   - Live context access: reads logged-in student's pending complaints, queries specific complaint IDs (`CMP-2026-XXXX`), displays warden hours, and suggests clickable quick-navigation actions.

---

## 5. Demo Accounts & Rapid Testing

The application includes clearly labelled, 1-tap demo credentials on the login screen:

| Role | Name | Email | Password | Details |
|---|---|---|---|---|
| **Resident Student** | Aarav Sharma | `aarav.student@dormdesk.edu` | `resident123` | Tagore Hostel, Block B, Room 204 |
| **Hostel Warden** | Dr. Rajesh Mehta | `warden.mehta@dormdesk.edu` | `warden123` | Tagore Hostel Administration, Office 01 |

Pre-populated dataset includes:
- **8 Students** (Aarav, Diya, Rohan, Ananya, Karan, Sneha, Vikram, Priya).
- **2 Wardens** (Dr. Rajesh Mehta, Mrs. Sunita Sharma).
- **15 Realistic Complaints** across all categories, stages, and priorities.
- **8 Announcements** including pinned water tank cleaning & fire drill notices.
- **10 Feedback Submissions** with warden responses.
- Sample in-app notifications.

---

## 6. Project Structure

```
DormDesk/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/dormdesk/app/
│       │   │   ├── DormDeskApplication.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── model/         # User, Complaint, Feedback, Announcement, Notification, ChatMessage
│       │   │   │   ├── repository/    # DormDeskRepository, InMemoryDormDeskRepository, FirebaseDormDeskRepository
│       │   │   │   └── remote/        # GroqChatApi (OkHttp client for FastAPI)
│       │   │   ├── viewmodel/         # Auth, StudentDashboard, Complaints, Feedback, Announcements, Chatbot, Warden
│       │   │   ├── utils/             # DateUtils, IdGenerator, FaqEngine
│       │   │   └── ui/
│       │   │       ├── theme/         # Color, Type, Theme (Warm Ivory, Espresso Brown palette)
│       │   │       ├── components/    # TopBar, BottomNav, StatusBadge, PriorityBadge, TimelineView, StarRating
│       │   │       ├── navigation/    # Screen, DormDeskNavHost
│       │   │       ├── auth/          # SplashScreen, LoginScreen, RegisterScreen
│       │   │       ├── student/       # StudentHomeScreen
│       │   │       ├── complaints/    # RaiseComplaintScreen, ComplaintHistoryScreen, ComplaintDetailScreen
│       │   │       ├── feedback/      # StudentFeedbackScreen
│       │   │       ├── announcements/ # AnnouncementsScreen
│       │   │       ├── chatbot/       # DormDeskAIChatScreen
│       │   │       ├── notifications/ # NotificationCenterScreen
│       │   │       ├── profile/       # ProfileScreen
│       │   │       └── admin/         # WardenDashboard, WardenComplaints, WardenFeedback, WardenAnnouncements, WardenAnalytics
│       │   └── res/                   # Drawables, mipmap icons, values (strings, colors, themes)
│       └── test/                      # Unit tests (FaqEngineTest, ComplaintLifecycleTest, IdGeneratorAndDateTest)
├── backend/                           # FastAPI Python Microservice for Groq AI
│   ├── main.py                        # FastAPI application & endpoints
│   ├── models.py                      # Pydantic schemas
│   ├── groq_service.py                # Groq API client with fallback
│   ├── faq_data.py                    # Rule-based fallback FAQ engine
│   ├── requirements.txt               # Dependencies
│   ├── .env.example                   # Environment variable template
│   └── README.md                      # Backend setup instructions
├── build.gradle.kts                   # Root build script
├── settings.gradle.kts                # Project settings
├── gradle.properties                  # JVM & AndroidX properties
└── firebase-rules.json                # Firebase Realtime Database Security Rules
```

---

## 7. How to Run

### Running the Android Application in Android Studio
1. Open **Android Studio** (Flamingo, Giraffe, Hedgehog, Iguana or newer).
2. Choose **File > Open** and select `c:\Users\Himanshi Arora\Downloads\DormDesk`.
3. Allow Gradle to sync dependencies.
4. Select an Android Emulator or physical device (API 24 to 34).
5. Press **Run (Shift + F10)**.

### Running the Groq AI FastAPI Backend (Optional)
The Android app works 100% out-of-the-box using the internal local FAQ engine! To enable generative Groq AI:
```bash
cd backend
python -m venv venv
venv\Scripts\activate       # Windows
pip install -r requirements.txt
copy .env.example .env
# Edit .env and enter your free GROQ_API_KEY from https://console.groq.com
python main.py
```
*(On Android Emulator, `http://10.0.2.2:8000` automatically resolves to the host machine's localhost)*.

---

## 8. CSE226 Academic Viva Concept Guide

1. **Composable Functions**: Pure declarative Kotlin functions emitting UI (`@Composable`). Recomposition runs automatically when observable state changes.
2. **State Management**: Using `StateFlow` and `collectAsState()` to expose immutable state streams from ViewModels to Compose screens.
3. **Unidirectional Data Flow (UDF)**: Events flow up from UI to ViewModel; state flows down from ViewModel to UI.
4. **Repository Pattern**: Shields ViewModels from data source implementation details (swappable between in-memory mock and Firebase).
5. **Coroutines**: Asynchronous execution via `viewModelScope.launch` preventing UI thread blocking.
6. **Navigation Compose**: Managing backstack, arguments, and bottom navigation without fragment transactions.
