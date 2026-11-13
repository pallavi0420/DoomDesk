# DormDesk AI Backend (FastAPI + Google Gemini)

Secure, asynchronous AI middleware for the **DormDesk** Android application.

## Overview

The DormDesk AI backend connects hostel residents with an intelligent virtual assistant powered by **Google Gemini API** (using models such as `gemini-1.5-flash` or `gemini-2.0-flash`).

If an API key is not provided, if quota is reached, or if network connectivity is unavailable, the service **automatically falls back to the embedded local hostel FAQ engine**, ensuring zero downtime for the Android app.

## Security Highlights

- The Gemini API key is strictly maintained on this backend and is **never** embedded in client-side Android source code or resources.
- Personal data and passwords are never passed to the LLM. Only authorized resident context (name, room, active complaints summary) is shared for contextual grounding.
- `.env` is excluded via `.gitignore` to prevent secret leaks.

## Local Setup & Execution

### 1. Create and Activate a Virtual Environment

```bash
cd backend
python -m venv venv

# On Windows (PowerShell):
.\venv\Scripts\Activate.ps1

# On macOS/Linux:
source venv/bin/activate
```

### 2. Install Dependencies

```bash
pip install -r requirements.txt
```

### 3. Configure Your Environment

Copy `.env.example` to `.env`:

```bash
copy .env.example .env
```

Edit `.env` and set your Gemini API key:

```env
GEMINI_API_KEY=AIzaSy...your_gemini_api_key
GEMINI_MODEL=gemini-1.5-flash
PORT=8000
```

*(If left blank or set to placeholder, the server will operate seamlessly using the local hostel FAQ fallback!)*

### 4. Run the Server

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

The server will be reachable at:
- **API Documentation (Swagger UI)**: `http://localhost:8000/docs`
- **Health Check**: `http://localhost:8000/api/health`
- **Chat Endpoint**: `POST http://localhost:8000/api/chat`
