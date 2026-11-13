"""
Local FAQ and Knowledge Base for DormDesk Hostel Support Assistant
"""

FAQS = [
    {
        "keywords": ["raise", "register", "complaint", "file", "lodge", "broken", "issue"],
        "answer": "To raise a complaint, go to 'Raise Complaint' from your Home screen or Complaints tab. Select category (e.g. Electrical, Plumbing, Cleaning, Wi-Fi), set priority, specify your room number and detailed description, and attach an optional photo.",
        "actionRoute": "student_complaint_raise"
    },
    {
        "keywords": ["status", "track", "progress", "timeline"],
        "answer": "You can track the live 7-step progress of your complaints under the 'Complaints' tab. The lifecycle stages are: 1. Submitted -> 2. Received -> 3. Assigned -> 4. In Progress -> 5. Resolution Updated -> 6. Resolved -> 7. Closed. Note that a complaint is only closed once you confirm resolution or provide your satisfaction rating.",
        "actionRoute": "student_complaints"
    },
    {
        "keywords": ["mess", "food", "breakfast", "lunch", "dinner", "meal", "timing"],
        "answer": "Hostel Mess Timings:\n• Breakfast: 7:30 AM – 9:30 AM\n• Lunch: 12:30 PM – 2:30 PM\n• Evening Snacks & Tea: 5:00 PM – 6:00 PM\n• Dinner: 7:30 PM – 9:30 PM\nSpecial festive meals are announced on the Notice Board.",
        "actionRoute": "student_notices"
    },
    {
        "keywords": ["water", "geyser", "drinking", "supply", "bath"],
        "answer": "Tap water supply operates daily between 5:30 AM - 10:30 AM and 5:00 PM - 9:30 PM. RO purified drinking water stations are active 24/7 on each floor lobby. For tap leakage or geyser problems, please raise a Plumbing complaint.",
        "actionRoute": "student_complaint_raise"
    },
    {
        "keywords": ["wifi", "wi-fi", "internet", "lan", "network", "speed"],
        "answer": "Connect to 'Hostel_HighSpeed_5G' SSID using your Student Registration ID and campus portal password. If you experience dead zones or connection drops, submit an 'Internet / Wi-Fi' complaint for access point inspection.",
        "actionRoute": "student_complaint_raise"
    },
    {
        "keywords": ["contact", "warden", "office", "admin", "timing", "phone"],
        "answer": "Hostel Warden Office Information:\n• Location: Ground Floor, Admin Block (Room G-02)\n• Visiting Hours: Mon - Sat: 9:00 AM – 1:00 PM & 4:00 PM – 6:00 PM\n• Email: warden@dormdesk.app\n• Emergency Mobile: +91 98765 43210\n• Security Gate Intercom: Ext. 101",
        "actionRoute": None
    },
    {
        "keywords": ["curfew", "gate", "night", "entry", "leave", "slip"],
        "answer": "Hostel main gates close promptly at 10:00 PM. Late entry requires a verified late pass from the warden office. Overnight leave requests must be approved through the parent portal before 6:00 PM.",
        "actionRoute": None
    },
    {
        "keywords": ["feedback", "suggestion", "review", "rate"],
        "answer": "You can share constructive suggestions or feedback on hostel facilities, mess quality, cleanliness, or staff behaviour under the 'Feedback' section. You can also choose to submit anonymously.",
        "actionRoute": "student_feedback"
    }
]

def search_local_faq(query: str, student_name: str = None, room_number: str = None, complaints_summary: str = None) -> dict:
    q = query.lower().strip()

    # Check for complaint status questions with student context
    if ("my complaint" in q or "pending" in q or "my status" in q) and complaints_summary:
        return {
            "reply": f"Here is the active complaint status for {student_name or 'your room'} (Room {room_number or ''}):\n\n{complaints_summary}\n\nYou can tap below to view the full 7-step timeline.",
            "source": "local_faq",
            "isFallback": True,
            "actionRoute": "student_complaints"
        }

    for item in FAQS:
        if any(keyword in q for keyword in item["keywords"]):
            return {
                "reply": item["answer"],
                "source": "local_faq",
                "isFallback": True,
                "actionRoute": item["actionRoute"]
            }

    return {
        "reply": f"Hello {student_name or 'Resident'}! I am DormDesk AI. I can assist you with lodging complaints, checking maintenance statuses, explaining hostel regulations, or reviewing notices. How can I help you today?",
        "source": "local_faq",
        "isFallback": True,
        "actionRoute": None
    }
