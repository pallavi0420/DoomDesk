from typing import List, Optional
from pydantic import BaseModel, Field


class ChatContext(BaseModel):
    user_id: Optional[str] = None
    user_name: Optional[str] = "Student"
    hostel: Optional[str] = "Tagore Hostel"
    room_number: Optional[str] = "204"
    pending_complaints_count: int = 0
    active_complaints_summary: Optional[str] = ""
    latest_announcement_title: Optional[str] = ""


class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, description="Student's message or query")
    conversation_history: Optional[List[dict]] = Field(default_factory=list)
    context: Optional[ChatContext] = Field(default_factory=ChatContext)


class ChatResponse(BaseModel):
    response: str
    source: str = "groq"  # "groq" or "local_faq_fallback"
    model_used: Optional[str] = None
    suggested_actions: List[str] = Field(default_factory=list)
    target_route: Optional[str] = None


class HealthResponse(BaseModel):
    status: str
    service: str = "DormDesk AI Backend"
    groq_configured: bool
    model: str
