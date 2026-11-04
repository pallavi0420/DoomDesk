package com.dormdesk.app.utils

import com.dormdesk.app.data.model.Announcement
import com.dormdesk.app.data.model.Complaint
import com.dormdesk.app.data.model.User

data class FaqResult(
    val answer: String,
    val suggestedActions: List<String> = emptyList(),
    val targetRoute: String? = null
)

object FaqEngine {

    fun processQuery(
        rawQuery: String,
        currentUser: User?,
        userComplaints: List<Complaint>,
        recentAnnouncements: List<Announcement>
    ): FaqResult {
        val query = rawQuery.trim().lowercase()

        // 1. Complaint Status / Pending Complaints Check
        if (query.contains("pending") || query.contains("my complaint status") || query.contains("status of my") || query.contains("track complaint") || (query.contains("status") && query.contains("complaint"))) {
            val pendingList = userComplaints.filter { it.status.isActive }
            return if (pendingList.isEmpty()) {
                FaqResult(
                    answer = "Great news, ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Resident"}! You currently have no pending complaints. All your submitted requests have either been resolved or closed. If you are experiencing any new issues with your room or facilities, feel free to raise a new complaint.",
                    suggestedActions = listOf("How do I raise a complaint?", "What are the latest announcements?", "How do I submit feedback?"),
                    targetRoute = "complaints"
                )
            } else {
                val summary = pendingList.take(3).joinToString("\n\n") { cmp ->
                    "• *${cmp.complaintId}*: ${cmp.title}\n  Category: ${cmp.category.label} | Priority: ${cmp.priority.label}\n  Status: *${cmp.status.label}*${if (!cmp.assignedStaffName.isNullOrBlank()) "\n  Assigned: ${cmp.assignedStaffName}" else ""}"
                }
                val moreCount = pendingList.size - 3
                val moreText = if (moreCount > 0) "\n...plus $moreCount other active ticket(s)." else ""

                FaqResult(
                    answer = "You currently have *${pendingList.size} active complaint(s)*:\n\n$summary$moreText\n\nYou can tap below to view full progress timelines, warden remarks, and resolution updates.",
                    suggestedActions = listOf("Show my complaints", "How do I contact the warden?"),
                    targetRoute = "complaints"
                )
            }
        }

        // 2. Specific Complaint ID lookup (e.g., CMP-2026-1001)
        val cmpIdMatch = Regex("cmp-\\d{4}-\\d{4}").find(query)?.value?.uppercase()
        if (cmpIdMatch != null) {
            val found = userComplaints.find { it.complaintId.equals(cmpIdMatch, ignoreCase = true) }
            return if (found != null) {
                FaqResult(
                    answer = "Here is the status for *${found.complaintId}* (${found.title}):\n\n• Current Stage: *${found.status.label}*\n• Category: ${found.category.label}\n• Priority: ${found.priority.label}\n• Location: ${found.location}\n• Assigned Staff: ${found.assignedStaffName ?: "Pending allocation"}\n• Remarks: ${found.remarks ?: "Under administrative review"}",
                    suggestedActions = listOf("Show my complaints", "How do I raise a complaint?")
                )
            } else {
                FaqResult(
                    answer = "I could not locate a complaint with ID *$cmpIdMatch* in your student account records. Please verify the ID on your complaints tab.",
                    suggestedActions = listOf("Show my complaints")
                )
            }
        }

        // 3. How to raise a complaint
        if (query.contains("raise") || query.contains("file") || query.contains("submit complaint") || query.contains("new complaint") || query.contains("register complaint")) {
            return FaqResult(
                answer = "To raise a new hostel complaint:\n\n1. Go to the **Complaints** tab or tap **Raise Complaint** on your Home screen.\n2. Tap the **+ New Complaint** button.\n3. Enter a clear title, select the correct category (e.g., Electrical, Plumbing, Cleaning).\n4. Choose the priority level (Low, Medium, High, or Urgent for critical hazards).\n5. Specify your exact room or common area location.\n6. Optionally attach a photograph showing the issue.\n7. Tap **Submit Complaint**.\n\nA unique complaint ID will be generated immediately, and the warden will be notified.",
                suggestedActions = listOf("Show my pending complaints", "How do I contact the warden?"),
                targetRoute = "raise_complaint"
            )
        }

        // 4. Latest hostel announcements
        if (query.contains("announcement") || query.contains("notice") || query.contains("news") || query.contains("updates") || query.contains("bulletin")) {
            val notices = recentAnnouncements.take(3)
            return if (notices.isEmpty()) {
                FaqResult(
                    answer = "There are no new hostel announcements posted right now. Please check back later or check the notice board in the lobby.",
                    suggestedActions = listOf("How do I contact the warden?", "Show my pending complaints")
                )
            } else {
                val noticeSummary = notices.joinToString("\n\n") { n ->
                    "📢 *${n.title}* [${n.category.label}]\n${n.description.take(90)}..."
                }
                FaqResult(
                    answer = "Here are the latest hostel notices:\n\n$noticeSummary\n\nVisit the **Notices** tab for complete details.",
                    suggestedActions = listOf("Go to Notices", "How do I contact the warden?"),
                    targetRoute = "announcements"
                )
            }
        }

        // 5. Contact the warden / office hours
        if (query.contains("contact warden") || query.contains("warden contact") || query.contains("warden phone") || query.contains("warden email") || query.contains("office hours") || query.contains("warden")) {
            return FaqResult(
                answer = "🏢 **Warden Office & Contact Details**:\n\n• **Chief Warden**: Dr. Rajesh Mehta\n• **Office**: Tagore Hostel Administration, Room 01 (Ground Floor)\n• **Office Hours**: 04:30 PM – 07:00 PM (Monday to Friday)\n• **Emergency Helpline**: +91 94123 78901\n• **Official Email**: warden.mehta@dormdesk.edu\n\nFor maintenance issues, we encourage logging a ticket on DormDesk so all repairs are digitally audited and tracked.",
                suggestedActions = listOf("How do I raise a complaint?", "What are the latest announcements?")
            )
        }

        // 6. Submit feedback
        if (query.contains("feedback") || query.contains("suggestion") || query.contains("mess food") || query.contains("mess feedback")) {
            return FaqResult(
                answer = "We value your voice! To submit feedback or suggestions:\n\n1. Navigate to the **Feedback** section from Quick Actions.\n2. Choose a category (Mess & Food, Cleanliness, Facilities, Staff Behaviour, General).\n3. Provide a star rating (1 to 5) and write your detailed observations.\n4. You can also toggle **Submit Anonymously** if you prefer your name not to appear to the hostel committee.\n5. The warden regularly reviews submissions and responds directly.",
                suggestedActions = listOf("Submit Feedback", "How do I contact the warden?"),
                targetRoute = "feedback"
            )
        }

        // 7. Complaint lifecycle / statuses explanation
        if (query.contains("lifecycle") || query.contains("status mean") || query.contains("stages") || query.contains("timeline")) {
            return FaqResult(
                answer = "Every DormDesk complaint goes through 7 clear stages:\n\n1. **Submitted**: Ticket registered in the system.\n2. **Received by Warden**: Warden reviewed the issue.\n3. **Maintenance Assigned**: Electrician/plumber/technician designated.\n4. **Work in Progress**: Physical repair is underway.\n5. **Resolution Updated**: Technician completed the task with remarks.\n6. **Resolved**: Warden reviewed completion.\n7. **Closed**: Final closure confirmed by you or justified by the office.\n\n*Note*: You will be asked to confirm and rate the resolution before a ticket is permanently closed!",
                suggestedActions = listOf("Show my pending complaints", "How do I raise a complaint?")
            )
        }

        // 8. Wi-Fi / Internet issues
        if (query.contains("wifi") || query.contains("wi-fi") || query.contains("internet") || query.contains("network") || query.contains("lan")) {
            return FaqResult(
                answer = "For Hostel Wi-Fi issues:\n\n• Network SSID: `Campus_Hostel_Secure`\n• Login Portal: Authenticate using your Student ID & Campus password.\n• If the router on your floor has no blinking internet indicator, raise a complaint under the **Internet / Wi-Fi** category with your room number and corridor wing.",
                suggestedActions = listOf("How do I raise a complaint?", "Show my pending complaints")
            )
        }

        // 9. Water supply / Electricity timings
        if (query.contains("water") || query.contains("electricity") || query.contains("power") || query.contains("geyser")) {
            return FaqResult(
                answer = "💧 **Hot Water Timings**: 06:00 AM – 10:30 AM & 06:30 PM – 09:30 PM daily.\n⚡ **Backup Generator**: In the event of grid interruption, automatic backup kicks in within 90 seconds for rooms and corridors.\n\nIf your room has an isolated MCB trip or tap leakage, please file a complaint immediately.",
                suggestedActions = listOf("How do I raise a complaint?", "What are the latest announcements?")
            )
        }

        // 10. Default fallback with helpful prompts
        return FaqResult(
            answer = "Hello! I am **DormDesk AI**, your 24/7 hostel resident assistant. I can help you with:\n\n• Checking the progress of your pending complaints.\n• Guiding you through filing maintenance tickets.\n• Sharing the latest hostel announcements.\n• Providing warden office hours & emergency contacts.\n• Answering common questions about mess, Wi-Fi, and water timings.\n\nHow may I help you today?",
            suggestedActions = listOf(
                "What is my complaint status?",
                "How do I raise a complaint?",
                "What are the latest hostel announcements?",
                "How can I contact the warden?",
                "How do I submit feedback?"
            )
        )
    }
}
