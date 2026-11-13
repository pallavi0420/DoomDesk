package com.dormdesk.app.data.remote

import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.User

object LocalFaqEngine {

    val suggestedQuestions = listOf(
        "How do I raise a complaint?",
        "What is my complaint status?",
        "Show my pending complaints",
        "What are the latest hostel announcements?",
        "How can I contact the warden?",
        "How do I submit feedback?"
    )

    fun generateResponse(
        userQuery: String,
        currentUser: User?,
        userComplaints: List<Complaint>,
        announcements: List<Announcement>
    ): ChatResponse {
        val query = userQuery.trim().lowercase()

        // 1. Raise Complaint
        if (query.contains("raise") || query.contains("register") || query.contains("lodge") || query.contains("file a complaint") || query.contains("create complaint")) {
            return ChatResponse(
                reply = "To raise a complaint:\n1. Tap 'Raise Complaint' on your Home dashboard or Complaints tab.\n2. Select category (e.g., Electrical, Plumbing, Internet).\n3. Set priority (Low, Medium, High, Urgent).\n4. Specify location/room and detailed description.\n5. Optionally attach a photo.\n6. Submit to notify the Warden immediately.",
                source = "local_faq",
                isFallback = true,
                actionRoute = "student_complaint_raise"
            )
        }

        // 2. Complaint Status / Pending Complaints
        if (query.contains("status") || query.contains("my complaint") || query.contains("pending complaint") || query.contains("track")) {
            if (userComplaints.isEmpty()) {
                return ChatResponse(
                    reply = "You currently have no registered complaints on file for ${currentUser?.fullName ?: "your account"}. If you are facing any hostel issue, tap 'Raise Complaint' below.",
                    source = "local_faq",
                    isFallback = true,
                    actionRoute = "student_complaint_raise"
                )
            }

            val pending = userComplaints.filter { !it.isClosed }
            val replyText = buildString {
                append("Here is your complaint summary:\n\n")
                if (pending.isEmpty()) {
                    append("All your complaints are marked resolved or closed! Great job.\n")
                } else {
                    append("You have ${pending.size} active complaint(s):\n")
                    pending.take(3).forEach { c ->
                        append("• #${c.complaintId}: \"${c.title}\" — Status: ${c.status.displayName}")
                        if (c.assignedStaffName.isNotBlank()) {
                            append(" (Assigned: ${c.assignedStaffName})")
                        }
                        append("\n")
                    }
                    if (pending.size > 3) {
                        append("...and ${pending.size - 3} more.\n")
                    }
                }
                append("\nTap below to view full timeline and details.")
            }

            return ChatResponse(
                reply = replyText,
                source = "local_faq",
                isFallback = true,
                actionRoute = "student_complaints"
            )
        }

        // 3. Announcements
        if (query.contains("announcement") || query.contains("notice") || query.contains("update") || query.contains("bulletin")) {
            if (announcements.isEmpty()) {
                return ChatResponse(
                    reply = "There are no active notices on the hostel bulletin board right now.",
                    source = "local_faq",
                    isFallback = true
                )
            }

            val replyText = buildString {
                append("Latest Hostel Notices:\n\n")
                announcements.take(3).forEach { notice ->
                    val pin = if (notice.isPinned) "📌 " else ""
                    append("$pin[${notice.category.displayName}] ${notice.title}\n${notice.description.take(90)}...\n\n")
                }
                append("Check the Notices tab for full details and emergency updates.")
            }

            return ChatResponse(
                reply = replyText,
                source = "local_faq",
                isFallback = true,
                actionRoute = "student_notices"
            )
        }

        // 4. Contact Warden
        if (query.contains("contact") || query.contains("warden") || query.contains("office") || query.contains("admin")) {
            return ChatResponse(
                reply = "Hostel Warden Office Information:\n• Office Location: Ground Floor, Admin Block (Room G-02)\n• Visiting Hours: Mon - Sat: 9:00 AM – 1:00 PM & 4:00 PM – 6:00 PM\n• Email: warden@dormdesk.app\n• Emergency Helplines: +91 98765 43210 / Security Gate: Ext. 101\n• For non-urgent hostel matters, please submit a complaint or feedback via DormDesk.",
                source = "local_faq",
                isFallback = true
            )
        }

        // 5. Feedback
        if (query.contains("feedback") || query.contains("suggestion") || query.contains("rating") || query.contains("review")) {
            return ChatResponse(
                reply = "You can submit feedback or suggestions about Hostel Facilities, Mess & Food, Cleanliness, or Staff Behaviour.\n\nYou can also submit anonymously if preferred. The Warden reviews all feedback and provides administrative responses.",
                source = "local_faq",
                isFallback = true,
                actionRoute = "student_feedback"
            )
        }

        // 6. Mess / Food
        if (query.contains("mess") || query.contains("food") || query.contains("breakfast") || query.contains("lunch") || query.contains("dinner") || query.contains("meal")) {
            return ChatResponse(
                reply = "Hostel Mess Schedule:\n• Breakfast: 7:30 AM – 9:30 AM\n• Lunch: 12:30 PM – 2:30 PM\n• Evening Tea & Snacks: 5:00 PM – 6:00 PM\n• Dinner: 7:30 PM – 9:30 PM\n\nFor quality complaints, raise a complaint under 'Mess / Food' or submit feedback.",
                source = "local_faq",
                isFallback = true
            )
        }

        // 7. Water / Electricity
        if (query.contains("water") || query.contains("drinking") || query.contains("power") || query.contains("electricity") || query.contains("current")) {
            return ChatResponse(
                reply = "Water & Utilities Notice:\n• Tap Water Supply: 5:30 AM – 10:30 AM & 5:00 PM – 9:30 PM daily.\n• RO Water Dispensers are available 24/7 on each floor lobby.\n• For water geyser or electrical issues in your room, register an Electrical or Plumbing complaint for prompt technician dispatch.",
                source = "local_faq",
                isFallback = true,
                actionRoute = "student_complaint_raise"
            )
        }

        // 8. Wi-Fi / Internet
        if (query.contains("wifi") || query.contains("wi-fi") || query.contains("internet") || query.contains("network") || query.contains("lan")) {
            return ChatResponse(
                reply = "Hostel Wi-Fi Assistance:\n1. Connect to SSID: 'Hostel_HighSpeed_5G'\n2. Login via captive portal using your Student ID (${currentUser?.studentId ?: "ID"}) and university password.\n3. If your floor router is offline or speed is degraded, raise an 'Internet / Wi-Fi' complaint.",
                source = "local_faq",
                isFallback = true,
                actionRoute = "student_complaint_raise"
            )
        }

        // 9. Status Workflow Explanation
        if (query.contains("explain") || query.contains("workflow") || query.contains("resolution updated") || query.contains("timeline")) {
            return ChatResponse(
                reply = "DormDesk 7-Step Complaint Lifecycle:\n1. Submitted: Registered by resident\n2. Received: Acknowledged by Warden\n3. Assigned: Maintenance technician assigned\n4. In Progress: Physical repair ongoing\n5. Resolution Updated: Work finished with notes\n6. Resolved: Warden verified completion\n7. Closed: Student confirmed satisfaction or admin closed.\n\nNote: A complaint is only closed after resident confirmation or formal closing reason.",
                source = "local_faq",
                isFallback = true
            )
        }

        // Default Intelligent Fallback
        return ChatResponse(
            reply = "Hello ${currentUser?.fullName ?: "Resident"}! I am DormDesk AI, your 24/7 smart hostel assistant. I can help you track complaints, explain hostel regulations, review notices, or contact the warden office. Try tapping one of the suggested questions below!",
            source = "local_faq",
            isFallback = true
        )
    }
}
