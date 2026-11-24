import os
import sys
import asyncio
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
from dotenv import load_dotenv

if sys.platform == "win32":
    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

from app.gemini_service import query_gemini_or_fallback
from app.faq_data import FAQS

# Load .env if present
load_dotenv()

app = FastAPI(
    title="DormDesk AI Backend",
    description="Secure FastAPI backend providing xAI Grok / Google Gemini API integration and Local FAQ fallback for the DormDesk Android App",
    version="1.0.0"
)

# Enable CORS for Android clients and web inspection
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class HistoryMessageDto(BaseModel):
    role: str
    content: str

class ChatRequestDto(BaseModel):
    message: str
    studentId: Optional[str] = None
    studentName: Optional[str] = None
    roomNumber: Optional[str] = None
    activeComplaintsSummary: Optional[str] = None
    conversationHistory: Optional[List[HistoryMessageDto]] = None

class ChatResponseDto(BaseModel):
    reply: str
    source: str = "gemini"
    isFallback: bool = False
    actionRoute: Optional[str] = None

class HealthResponseDto(BaseModel):
    status: str
    aiConfigured: bool
    provider: str
    model: str

@app.get("/", tags=["Root"])
def root():
    grok_key = (os.getenv("GROK_API_KEY") or os.getenv("XAI_API_KEY") or "").strip()
    gemini_key = os.getenv("GEMINI_API_KEY", "").strip()
    provider = "xAI Grok" if (grok_key and grok_key != "your_grok_api_key_here") else ("Google Gemini" if (gemini_key and gemini_key != "your_gemini_api_key_here") else "Offline FAQs")
    return {
        "app": "DormDesk AI Backend",
        "tagline": "Your Hostel. Your Voice. One Desk.",
        "provider": provider,
        "status": "operational"
    }

@app.get("/api/health", response_model=HealthResponseDto, tags=["Health"])
def health_check():
    grok_key = (os.getenv("GROK_API_KEY") or os.getenv("XAI_API_KEY") or "").strip()
    grok_model = os.getenv("GROK_MODEL", "grok-2-latest").strip()
    gemini_key = os.getenv("GEMINI_API_KEY", "").strip()
    gemini_model = os.getenv("GEMINI_MODEL", "gemini-3.5-flash-lite").strip()

    if grok_key and grok_key not in ["your_grok_api_key_here", "your_xai_api_key_here"]:
        return HealthResponseDto(
            status="ok",
            aiConfigured=True,
            provider="xAI Grok",
            model=grok_model
        )
    elif gemini_key and gemini_key != "your_gemini_api_key_here":
        return HealthResponseDto(
            status="ok",
            aiConfigured=True,
            provider="Google Gemini",
            model=gemini_model
        )
    else:
        return HealthResponseDto(
            status="ok",
            aiConfigured=False,
            provider="Offline / Local FAQs",
            model="local-rule-engine"
        )

@app.post("/api/chat", response_model=ChatResponseDto, tags=["Chat"])
async def chat_endpoint(payload: ChatRequestDto):
    if not payload.message or not payload.message.strip():
        raise HTTPException(status_code=400, detail="Message cannot be empty")

    history_dicts = None
    if payload.conversationHistory:
        history_dicts = [{"role": h.role, "content": h.content} for h in payload.conversationHistory]

    result = await query_gemini_or_fallback(
        user_message=payload.message,
        student_id=payload.studentId,
        student_name=payload.studentName,
        room_number=payload.roomNumber,
        active_complaints_summary=payload.activeComplaintsSummary,
        conversation_history=history_dicts
    )

    return ChatResponseDto(
        reply=result["reply"],
        source=result.get("source", "local_faq"),
        isFallback=result.get("isFallback", False),
        actionRoute=result.get("actionRoute")
    )

@app.get("/api/faq", tags=["FAQ"])
def get_all_faqs():
    return FAQS

if __name__ == "__main__":
    import uvicorn
    port = int(os.getenv("PORT", "8000"))
    uvicorn.run("app.main:app", host="0.0.0.0", port=port, reload=True)
