import os
import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv

from models import ChatRequest, ChatResponse, HealthResponse
from groq_service import GroqService

load_dotenv()

app = FastAPI(
    title="DormDesk AI Backend",
    description="Secure AI gateway for DormDesk Hostel Resident Support App powered by Groq with Local FAQ Fallback",
    version="1.0.0"
)

# Enable CORS for local Android development (10.0.2.2 emulator, local Wi-Fi, etc.)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

groq_service = GroqService()


@app.get("/", tags=["Info"])
async def root():
    return {
        "app": "DormDesk AI Resident Support Service",
        "tagline": "Your Hostel. Your Voice. One Desk.",
        "status": "online",
        "groq_configured": groq_service.is_configured(),
        "model": groq_service.model
    }


@app.get("/health", response_model=HealthResponse, tags=["Health"])
async def health_check():
    return HealthResponse(
        status="healthy",
        service="DormDesk AI Backend",
        groq_configured=groq_service.is_configured(),
        model=groq_service.model
    )


@app.post("/api/chat", response_model=ChatResponse, tags=["Chat"])
async def chat_endpoint(request: ChatRequest):
    """
    Primary resident chat endpoint.
    Processes queries using Groq LLM when configured, or safely routes
    to the rule-based hostel FAQ fallback engine.
    """
    try:
        context_dict = request.context.model_dump() if request.context else {}
        history_list = request.conversation_history or []

        result = await groq_service.generate_response(
            message=request.message,
            conversation_history=history_list,
            context=context_dict
        )

        return ChatResponse(
            response=result["response"],
            source=result.get("source", "groq"),
            model_used=result.get("model_used"),
            suggested_actions=result.get("suggested_actions", []),
            target_route=result.get("target_route")
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"AI Processing error: {str(e)}")


if __name__ == "__main__":
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8000"))
    uvicorn.run("main:app", host=host, port=port, reload=True)
