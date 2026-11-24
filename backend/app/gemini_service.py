import os
import httpx
from typing import List, Dict, Optional
from dotenv import load_dotenv
from app.faq_data import search_local_faq

load_dotenv()

GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
GROK_BASE_URL = "https://api.x.ai/v1/chat/completions"

SYSTEM_PROMPT = """You are DormDesk AI, a helpful, polite, and professional hostel management assistant for residents of Aryabhatta Hostel.
Tagline: "Your Hostel. Your Voice. One Desk."

Role & Responsibilities:
1. Help hostel residents report room maintenance complaints (Electrical, Plumbing, Wi-Fi, Furniture, Cleanliness, Washroom, Water Supply).
2. Explain the 7-step complaint lifecycle: 1. Submitted -> 2. Received by Warden -> 3. Maintenance Assigned -> 4. Work in Progress -> 5. Resolution Updated -> 6. Resolved -> 7. Closed.
3. Emphasize that a complaint is only closed after resident confirmation or formal administrative closure.
4. Guide residents on mess timings (Breakfast 7:30-9:30 AM, Lunch 12:30-2:30 PM, Snacks 5-6 PM, Dinner 7:30-9:30 PM), gate closing (10:00 PM), and water supply hours (5:30-10:30 AM & 5:00-9:30 PM).
5. Maintain strict privacy: only discuss the current resident's data. Never expose passwords or other students' private data.
6. Keep your answers concise, practical, and formatted cleanly.
"""

def determine_action_route(user_message: str) -> Optional[str]:
    lower_query = user_message.lower()
    if any(k in lower_query for k in ["raise", "lodge", "report", "new complaint"]):
        return "student_complaint_raise"
    elif any(k in lower_query for k in ["my complaint", "status", "pending", "active tickets"]):
        return "student_complaints"
    elif any(k in lower_query for k in ["notice", "announcement", "bulletin", "circular"]):
        return "student_notices"
    elif any(k in lower_query for k in ["feedback", "suggestion", "review"]):
        return "student_feedback"
    return None

async def query_gemini_or_fallback(
    user_message: str,
    student_id: Optional[str] = None,
    student_name: Optional[str] = None,
    room_number: Optional[str] = None,
    active_complaints_summary: Optional[str] = None,
    conversation_history: Optional[List[Dict[str, str]]] = None
) -> Dict:
    # 1. Check for xAI Grok API Key first
    grok_api_key = (os.getenv("GROK_API_KEY") or os.getenv("XAI_API_KEY") or "").strip()
    grok_model = os.getenv("GROK_MODEL", "grok-2-latest").strip()

    # 2. Check for Google Gemini API Key
    gemini_api_key = os.getenv("GEMINI_API_KEY", "").strip()
    gemini_model = os.getenv("GEMINI_MODEL", "gemini-3.5-flash-lite").strip()

    # Build context prompt
    user_context = f"Student Name: {student_name or 'Resident'}, ID: {student_id or 'N/A'}, Room: {room_number or 'N/A'}."
    if active_complaints_summary:
        user_context += f"\nCurrent Active Complaints: {active_complaints_summary}"

    full_system_instruction = f"{SYSTEM_PROMPT}\n\nResident Context:\n{user_context}"

    # A) Try Grok (xAI) if configured
    if grok_api_key and grok_api_key not in ["your_grok_api_key_here", "your_xai_api_key_here"]:
        try:
            messages = [{"role": "system", "content": full_system_instruction}]
            if conversation_history:
                for hist in conversation_history[-6:]:
                    role = "user" if hist.get("role") == "user" else "assistant"
                    content = hist.get("content", "")
                    if content:
                        messages.append({"role": role, "content": content})
            messages.append({"role": "user", "content": user_message})

            payload = {
                "model": grok_model,
                "messages": messages,
                "temperature": 0.4,
                "max_tokens": 512
            }
            headers = {
                "Authorization": f"Bearer {grok_api_key}",
                "Content-Type": "application/json"
            }

            async with httpx.AsyncClient(timeout=10.0) as client:
                response = await client.post(GROK_BASE_URL, json=payload, headers=headers)
                if response.status_code == 200:
                    data = response.json()
                    choices = data.get("choices", [])
                    if choices:
                        reply_text = choices[0].get("message", {}).get("content", "")
                        if reply_text:
                            return {
                                "reply": reply_text.strip(),
                                "source": f"grok:{grok_model}",
                                "isFallback": False,
                                "actionRoute": determine_action_route(user_message)
                            }
        except Exception:
            pass  # Fall through to Gemini or local FAQ fallback

    # B) Try Gemini if configured
    if gemini_api_key and gemini_api_key != "your_gemini_api_key_here":
        try:
            contents = []
            if conversation_history:
                for hist in conversation_history[-6:]:
                    role = "user" if hist.get("role") == "user" else "model"
                    content = hist.get("content", "")
                    if content:
                        contents.append({"role": role, "parts": [{"text": content}]})

            contents.append({"role": "user", "parts": [{"text": user_message}]})
            endpoint_url = f"{GEMINI_BASE_URL}/{gemini_model}:generateContent?key={gemini_api_key}"

            payload = {
                "systemInstruction": {"parts": [{"text": full_system_instruction}]},
                "contents": contents,
                "generationConfig": {"temperature": 0.4, "maxOutputTokens": 512}
            }

            async with httpx.AsyncClient(timeout=8.0) as client:
                headers = {"Content-Type": "application/json"}
                response = await client.post(endpoint_url, json=payload, headers=headers)
                if response.status_code == 200:
                    data = response.json()
                    candidates = data.get("candidates", [])
                    if candidates:
                        parts = candidates[0].get("content", {}).get("parts", [])
                        reply_text = parts[0].get("text", "") if parts else ""
                        if reply_text:
                            return {
                                "reply": reply_text.strip(),
                                "source": f"gemini:{gemini_model}",
                                "isFallback": False,
                                "actionRoute": determine_action_route(user_message)
                            }
        except Exception:
            pass

    # C) Offline / Fallback FAQ engine
    return search_local_faq(
        query=user_message,
        student_name=student_name,
        room_number=room_number,
        complaints_summary=active_complaints_summary
    )
