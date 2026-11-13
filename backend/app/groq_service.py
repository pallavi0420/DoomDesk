import os
import httpx
from typing import List, Dict, Optional
from dotenv import load_dotenv
from app.faq_data import search_local_faq

load_dotenv()

GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"

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

async def query_groq_or_fallback(
    user_message: str,
    student_id: Optional[str] = None,
    student_name: Optional[str] = None,
    room_number: Optional[str] = None,
    active_complaints_summary: Optional[str] = None,
    conversation_history: Optional[List[Dict[str, str]]] = None
) -> Dict:
    api_key = os.getenv("GROQ_API_KEY", "").strip()
    model = os.getenv("GROQ_MODEL", "llama-3.3-70b-versatile").strip()

    # If key is missing or blank, use local FAQ engine immediately
    if not api_key or api_key == "your_groq_api_key_here":
        return search_local_faq(
            query=user_message,
            student_name=student_name,
            room_number=room_number,
            complaints_summary=active_complaints_summary
        )

    # Build context prompt
    user_context = f"Student Name: {student_name or 'Resident'}, ID: {student_id or 'N/A'}, Room: {room_number or 'N/A'}."
    if active_complaints_summary:
        user_context += f"\nCurrent Active Complaints: {active_complaints_summary}"

    messages = [
        {"role": "system", "content": f"{SYSTEM_PROMPT}\n\nResident Context:\n{user_context}"}
    ]

    # Append past conversation history
    if conversation_history:
        for hist in conversation_history[-6:]:
            role = "user" if hist.get("role") == "user" else "assistant"
            content = hist.get("content", "")
            if content:
                messages.append({"role": role, "content": content})

    messages.append({"role": "user", "content": user_message})

    # Call Groq API with 8s timeout
    try:
        async with httpx.AsyncClient(timeout=8.0) as client:
            headers = {
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json"
            }
            payload = {
                "model": model,
                "messages": messages,
                "temperature": 0.4,
                "max_tokens": 512
            }
            response = await client.post(GROQ_API_URL, json=payload, headers=headers)

            if response.status_code == 200:
                data = response.json()
                reply_text = data["choices"][0]["message"]["content"]

                # Determine any helpful deep link navigation action
                action_route = None
                lower_query = user_message.lower()
                if "raise" in lower_query or "lodge" in lower_query or "report" in lower_query:
                    action_route = "student_complaint_raise"
                elif "my complaint" in lower_query or "pending" in lower_query:
                    action_route = "student_complaints"
                elif "notice" in lower_query or "announcement" in lower_query:
                    action_route = "student_notices"
                elif "feedback" in lower_query:
                    action_route = "student_feedback"

                return {
                    "reply": reply_text,
                    "source": f"groq:{model}",
                    "isFallback": False,
                    "actionRoute": action_route
                }
            else:
                # API rate limit, quota exceeded or invalid key -> fallback to local FAQ
                return search_local_faq(
                    query=user_message,
                    student_name=student_name,
                    room_number=room_number,
                    complaints_summary=active_complaints_summary
                )

    except Exception:
        # Network error, timeout -> graceful local fallback
        return search_local_faq(
            query=user_message,
            student_name=student_name,
            room_number=room_number,
            complaints_summary=active_complaints_summary
        )
