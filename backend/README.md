# DormDesk AI Backend (FastAPI + Groq API)

Secure, modular Python FastAPI microservice providing resident support intelligence for the **DormDesk** Android Application.

## Key Features

1. **Secure API Key Handling**: Never exposed to mobile clients or source control.
2. **Groq LLM Integration**: Uses OpenAI-compatible endpoints (`https://api.groq.com/openai/v1`) with ultra-fast inference (`llama-3.3-70b-versatile` or `llama-3.1-8b-instant`).
3. **Local FAQ Fallback**: If `GROQ_API_KEY` is not provided, expired, or rate-limited, requests are instantly routed to the local hostel FAQ engine with zero downtime.
4. **Student Privacy**: Only relevant non-sensitive student context (name, room, active ticket count) is provided to help answer questions accurately.

---

## Setup & Running Locally

### 1. Create and Activate Virtual Environment
```bash
cd backend
python -m venv venv

# On Windows:
venv\Scripts\activate

# On macOS/Linux:
source venv/bin/activate
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
copy .env.example .env     # Windows
cp .env.example .env       # Linux / macOS
```

Edit `.env` and set your Groq API Key:
```env
GROQ_API_KEY=gsk_your_actual_groq_api_key_here
GROQ_MODEL=llama-3.3-70b-versatile
HOST=0.0.0.0
PORT=8000
```

> **Note**: If you leave `GROQ_API_KEY` blank or unset, the backend continues to work seamlessly via the internal FAQ fallback engine!

### 4. Start the Server
```bash
python main.py
```
Or directly with Uvicorn:
```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

The API will be available at:
- Documentation: `http://localhost:8000/docs`
- Health check: `http://localhost:8000/health`
- Chat endpoint: `POST http://localhost:8000/api/chat`

When testing with Android Emulator, use `http://10.0.2.2:8000` to connect to your host machine's localhost.
