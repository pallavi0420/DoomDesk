"""
Local FAQ and rule-based fallback responses for DormDesk AI.
Used when Groq API key is unconfigured, rate-limited, or unreachable.
"""

HOSTEL_FAQS = {
    "raise_complaint": {
        "keywords": ["raise", "file", "submit complaint", "new complaint", "broken", "repair", "register complaint"],
        "answer": (
            "To raise a hostel complaint:\n"
            "1. Open the **Complaints** tab or tap **Raise Complaint** on Home.\n"
            "2. Fill in the title, category (Electrical, Plumbing, Cleaning, etc.), and priority.\n"
            "3. Mention your exact room number or location.\n"
            "4. Attach an optional photo for faster verification.\n"
            "5. Submit! You will receive a unique Complaint ID (e.g., CMP-2026-XXXX) to track progress."
        ),
        "suggested_actions": ["Show my pending complaints", "How do I contact the warden?"],
        "target_route": "raise_complaint"
    },
    "complaint_status": {
        "keywords": ["status", "pending", "track", "my complaint", "progress", "update on"],
        "answer": (
            "You can track your complaint progress anytime under **My Complaints**.\n"
            "Every ticket follows 7 clear stages: Submitted ➔ Received by Warden ➔ Maintenance Assigned "
            "➔ Work in Progress ➔ Resolution Updated ➔ Resolved ➔ Closed.\n"
            "When the warden marks work completed, you will be prompted to verify and rate the resolution."
        ),
        "suggested_actions": ["Show my complaints", "How do I raise a complaint?"],
        "target_route": "complaints"
    },
    "warden_contact": {
        "keywords": ["warden", "contact", "office", "phone", "hours", "meet warden", "helpline"],
        "answer": (
            "🏢 **Warden Office Details**:\n"
            "• **Chief Warden**: Dr. Rajesh Mehta\n"
            "• **Office Location**: Tagore Hostel Admin Block, Room 01 (Ground Floor)\n"
            "• **Visiting Hours**: Monday–Friday, 4:30 PM – 7:00 PM\n"
            "• **Emergency Phone**: +91 94123 78901\n"
            "• **Email**: warden.mehta@dormdesk.edu"
        ),
        "suggested_actions": ["What are the latest announcements?", "How do I submit feedback?"],
        "target_route": None
    },
    "feedback": {
        "keywords": ["feedback", "suggestion", "mess food", "canteen", "cleanliness", "complaint about food"],
        "answer": (
            "To submit suggestions or feedback:\n"
            "1. Tap **Submit Feedback** from the quick actions.\n"
            "2. Select a category (Mess & Food, Cleanliness, Facilities, Staff Behaviour).\n"
            "3. Rate with 1 to 5 stars and add your thoughts.\n"
            "4. You may select **Submit Anonymously** if you prefer.\n"
            "The warden team reviews each submission and provides administrative replies."
        ),
        "suggested_actions": ["Submit Feedback", "How do I contact the warden?"],
        "target_route": "feedback"
    },
    "wifi_timing": {
        "keywords": ["wifi", "wi-fi", "internet", "lan", "network", "speed", "water", "electricity", "geyser"],
        "answer": (
            "ℹ️ **Hostel Amenities & Timings**:\n"
            "• **Wi-Fi SSID**: Campus_Hostel_Secure (Log in using Student ID credentials)\n"
            "• **Hot Water Timings**: 06:00 AM – 10:30 AM & 06:30 PM – 09:30 PM\n"
            "• **Quiet Hours**: 11:00 PM – 06:00 AM daily\n"
            "If your room has an isolated power or water disruption, please file an urgent complaint."
        ),
        "suggested_actions": ["How do I raise a complaint?", "Show my pending complaints"],
        "target_route": None
    }
}


def get_local_faq_response(query: str, context_summary: str = "") -> dict:
    q = query.lower().strip()

    for key, data in HOSTEL_FAQS.items():
        if any(kw in q for kw in data["keywords"]):
            ans = data["answer"]
            if context_summary:
                ans = f"{context_summary}\n\n{ans}"
            return {
                "response": ans,
                "source": "local_faq_fallback",
                "suggested_actions": data["suggested_actions"],
                "target_route": data.get("target_route")
            }

    # Default fallback
    return {
        "response": (
            "Hello! I am **DormDesk AI**, your hostel resident assistant. "
            "I can help you check your complaint status, file maintenance requests, "
            "check latest announcements, or contact hostel administration.\n\n"
            "How can I assist you today?"
        ),
        "source": "local_faq_fallback",
        "suggested_actions": [
            "How do I raise a complaint?",
            "What is my complaint status?",
            "What are the latest hostel announcements?",
            "How can I contact the warden?",
            "How do I submit feedback?"
        ],
        "target_route": None
    }
