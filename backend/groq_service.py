import os
import logging
from typing import Dict, Any, List
import httpx
from dotenv import load_dotenv
from faq_data import get_local_faq_response

load_dotenv()

logger = logging.getLogger("groq_service")
logging.basicConfig(level=logging.INFO)

GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
DEFAULT_MODEL = os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile")

SYSTEM_PROMPT = """You are DormDesk AI, the warm, polite, and efficient resident support assistant for hostel residents.
Your role:
- Answer student inquiries about hostel rules, mess timings, Wi-Fi, maintenance, and warden contacts.
- Assist residents with understanding their complaint status and the 7-stage complaint resolution process (Submitted -> Received -> Assigned -> Work in Progress -> Resolution Updated -> Resolved -> Closed).
- Guide students on how to file complaints and submit feedback.
- Never invent or fabricate information about other students or fake complaint updates.
- Keep answers concise, clear, and formatted in clean markdown with bullet points where appropriate.
- Maintain a respectful, supportive, and professional tone.
"""


class GroqService:
    def __init__(self):
        self.api_key = os.getenv("GROQ_API_KEY", "").strip()
        self.model = os.getenv("GROQ_MODEL", DEFAULT_MODEL).strip()

    def is_configured(self) -> bool:
        return bool(self.api_key and not self.api_key.startswith("your_groq"))

    async def generate_response(
        self,
        message: str,
        conversation_history: List[Dict[str, str]],
        context: Dict[str, Any]
    ) -> Dict[str, Any]:
        """
        Calls Groq API if configured and reachable; otherwise safely falls back to local FAQ logic.
        """
        # If API key is not configured or placeholder, use local FAQ immediately
        if not self.is_configured():
            logger.info("GROQ_API_KEY is not configured. Using local FAQ fallback.")
            return get_local_faq_response(message)

        # Build messages payload
        context_lines = []
        if context.get("user_name"):
            context_lines.append(f"Student Name: {context['user_name']}")
        if context.get("hostel") and context.get("room_number"):
            context_lines.append(f"Room: {context['hostel']}, Room {context['room_number']}")
        if "pending_complaints_count" in context:
            context_lines.append(f"Active Complaints Count: {context['pending_complaints_count']}")
        if context.get("active_complaints_summary"):
            context_lines.append(f"Student's Active Tickets: {context['active_complaints_summary']}")
        if context.get("latest_announcement_title"):
            context_lines.append(f"Latest Announcement: {context['latest_announcement_title']}")

        system_message = SYSTEM_PROMPT
        if context_lines:
            system_message += "\n\nStudent Account Context (Confidential):\n" + "\n".join(context_lines)

        messages = [{"role": "system", "content": system_message}]

        # Append limited history
        if conversation_history:
            for turn in conversation_history[-6:]:
                role = "assistant" if turn.get("role") in ["assistant", "ASSISTANT"] else "user"
                messages.append({"role": role, "content": turn.get("content", "")})

        messages.append({"role": "user", "content": message})

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

        payload = {
            "model": self.model,
            "messages": messages,
            "temperature": 0.4,
            "max_tokens": 512
        }

        try:
            async with httpx.AsyncClient(timeout=10.0) as client:
                res = await client.post(GROQ_API_URL, headers=headers, json=payload)

                if res.status_code == 200:
                    data = res.json()
                    content = data["choices"][0]["message"]["content"]
                    return {
                        "response": content,
                        "source": "groq",
                        "model_used": self.model,
                        "suggested_actions": [
                            "What is my complaint status?",
                            "How do I raise a complaint?",
                            "How can I contact the warden?"
                        ],
                        "target_route": None
                    }
                else:
                    logger.warning(
                        f"Groq API returned HTTP {res.status_code}: {res.text}. Falling back to local FAQ."
                    )
                    return get_local_faq_response(message)
        except Exception as e:
            logger.error(f"Error calling Groq API: {str(e)}. Falling back to local FAQ.")
            return get_local_faq_response(message)
