# DormDesk AI Backend (FastAPI + Groq)

Secure, asynchronous AI middleware for the **DormDesk** Android application.

## Overview

The DormDesk AI backend connects hostel residents with an intelligent virtual assistant powered by **Groq's OpenAI-compatible Chat Completions API** (using models such as `llama-3.3-70b-versatile` or `llama-3.1-8b-instant`).

If an API key is not provided, if the free quota is exhausted, or if network connectivity is unavailable, the service **automatically falls back to the embedded local hostel FAQ engine**, ensuring zero downtime for the Android app.

## Security Highlights

- The Groq API key is strictly maintained on this backend and is **never** embedded in client-side Android source code or resources.
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

Edit `.env` and set your Groq API key:

```env
GROQ_API_KEY=gsk_your_actual_groq_api_key
GROQ_MODEL=llama-3.3-70b-versatile
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

### Android Emulator Connectivity Note

When running on an Android Emulator:
- The emulator accesses your development machine's `localhost` via the standard alias `http://10.0.2.2:8000/`.
- DormDesk is already configured with `10.0.2.2:8000` out of the box in `RetrofitClient.kt`.
