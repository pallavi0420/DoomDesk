package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.*
import com.dormdesk.app.utils.IdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryDormDeskRepository : DormDeskRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    override val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    override val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _feedbacks = MutableStateFlow<List<FeedbackItem>>(emptyList())
    override val feedbacks: StateFlow<List<FeedbackItem>> = _feedbacks.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    override val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    override val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        loadDemoData()
    }

    private fun loadDemoData() {
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L
        val day = 24 * hour

        // 15 Sample Complaints
        val initialComplaints = listOf(
            Complaint(
                complaintId = "CMP-2026-1001",
                studentId = DemoAccounts.SampleStudent.uid,
                studentName = DemoAccounts.SampleStudent.fullName,
                studentPhone = DemoAccounts.SampleStudent.phone,
                hostel = DemoAccounts.SampleStudent.hostel,
                block = DemoAccounts.SampleStudent.block,
                roomNumber = DemoAccounts.SampleStudent.roomNumber,
                title = "Ceiling fan making squeaking noise and vibrating",
                description = "The fan in room 204 oscillates erratically at speed 4 and 5, accompanied by a loud metallic grinding sound.",
                category = ComplaintCategory.ELECTRICAL,
                priority = ComplaintPriority.MEDIUM,
                location = "Room 204, Tagore Hostel",
                imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
                status = ComplaintStatus.IN_PROGRESS,
                assignedStaffName = "Ramesh Kumar (Electrician)",
                remarks = "Capacitor and ball-bearing replacement scheduled for this afternoon.",
                createdAt = now - 2 * day,
                updatedAt = now - 4 * hour,
                activityLogs = listOf(
                    ActivityLogEntry(
                        id = "log-1",
                        timestamp = now - 2 * day,
                        action = "Complaint Submitted",
                        actorRole = "Student",
                        actorName = "Aarav Sharma",
                        note = "Ticket registered successfully."
                    ),
                    ActivityLogEntry(
                        id = "log-2",
                        timestamp = now - 1 * day - 18 * hour,
                        action = "Warden Acknowledged",
                        actorRole = "Warden",
                        actorName = "Dr. Rajesh Mehta",
                        note = "Marked for immediate electrical inspection."
                    ),
                    ActivityLogEntry(
                        id = "log-3",
                        timestamp = now - 18 * hour,
                        action = "Assigned Maintenance",
                        actorRole = "Warden",
                        actorName = "Dr. Rajesh Mehta",
                        note = "Assigned to Ramesh Kumar (Electrician)."
                    ),
                    ActivityLogEntry(
                        id = "log-4",
                        timestamp = now - 4 * hour,
                        action = "Work In Progress",
                        actorRole = "Staff",
                        actorName = "Ramesh Kumar",
                        note = "Inspection underway. Parts collected from central store."
                    )
                )
            ),
            Complaint(
                complaintId = "CMP-2026-1002",
                studentId = DemoAccounts.SampleStudent.uid,
                studentName = DemoAccounts.SampleStudent.fullName,
                studentPhone = DemoAccounts.SampleStudent.phone,
                hostel = DemoAccounts.SampleStudent.hostel,
                block = DemoAccounts.SampleStudent.block,
                roomNumber = DemoAccounts.SampleStudent.roomNumber,
                title = "Bathroom washbasin tap continuous dripping",
                description = "The cold water tap in the attached washroom leaks approximately 1 drop per second.",
                category = ComplaintCategory.PLUMBING,
                priority = ComplaintPriority.LOW,
                location = "Room 204 Bathroom",
                imageUrl = null,
                status = ComplaintStatus.RECEIVED,
                assignedStaffName = null,
                remarks = "Queued for floor plumbing round on Wednesday morning.",
                createdAt = now - 1 * day,
                updatedAt = now - 12 * hour,
                activityLogs = listOf(
                    ActivityLogEntry(
                        id = "log-10",
                        timestamp = now - 1 * day,
                        action = "Complaint Submitted",
                        actorRole = "Student",
                        actorName = "Aarav Sharma"
                    ),
                    ActivityLogEntry(
                        id = "log-11",
                        timestamp = now - 12 * hour,
                        action = "Warden Received",
                        actorRole = "Warden",
                        actorName = "Dr. Rajesh Mehta",
                        note = "Added to regular plumbing queue."
                    )
                )
            ),
            Complaint(
                complaintId = "CMP-2026-1003",
                studentId = DemoAccounts.SampleStudent.uid,
                studentName = DemoAccounts.SampleStudent.fullName,
                studentPhone = DemoAccounts.SampleStudent.phone,
                hostel = DemoAccounts.SampleStudent.hostel,
                block = DemoAccounts.SampleStudent.block,
                roomNumber = DemoAccounts.SampleStudent.roomNumber,
                title = "Study table drawer lock broken",
                description = "The central drawer lock cylinder popped out when turning the key.",
                category = ComplaintCategory.FURNITURE,
                priority = ComplaintPriority.LOW,
                location = "Room 204 Study Corner",
                imageUrl = null,
                status = ComplaintStatus.RESOLVED,
                assignedStaffName = "Sohan Singh (Carpenter)",
                remarks = "New lock fitted and verified with two keys delivered to resident.",
                resolutionNotes = "Replaced defective Godrej drawer lock cylinder. Smooth key operation confirmed.",
                isConfirmedByStudent = false,
                studentRating = 0,
                createdAt = now - 4 * day,
                updatedAt = now - 6 * hour,
                activityLogs = listOf(
                    ActivityLogEntry(
                        id = "log-20",
                        timestamp = now - 4 * day,
                        action = "Complaint Submitted",
                        actorRole = "Student",
                        actorName = "Aarav Sharma"
                    ),
                    ActivityLogEntry(
                        id = "log-21",
                        timestamp = now - 3 * day,
                        action = "Assigned",
                        actorRole = "Warden",
                        actorName = "Dr. Rajesh Mehta",
                        note = "Assigned to Sohan Singh"
                    ),
                    ActivityLogEntry(
                        id = "log-22",
                        timestamp = now - 6 * hour,
                        action = "Marked Resolved",
                        actorRole = "Warden",
                        actorName = "Dr. Rajesh Mehta",
                        note = "Work verified by floor supervisor. Awaiting resident confirmation."
                    )
                )
            ),
            Complaint(
                complaintId = "CMP-2026-1004",
                studentId = "stu_diya_patel",
                studentName = "Diya Patel",
                studentPhone = "+91 98765 43211",
                hostel = "Tagore Hostel",
                block = "Block A",
                roomNumber = "102",
                title = "Wi-Fi access point in 1st floor corridor offline",
                description = "SSID Campus_Hostel_Secure drops completely near rooms 101-110. AP amber LED blinking.",
                category = ComplaintCategory.INTERNET_WIFI,
                priority = ComplaintPriority.URGENT,
                location = "1st Floor Corridor East Wing",
                imageUrl = null,
                status = ComplaintStatus.ASSIGNED,
                assignedStaffName = "Amit Kumar (Network Admin)",
                remarks = "Switch port PoE reset attempted. Technician dispatching with replacement router.",
                createdAt = now - 5 * hour,
                updatedAt = now - 2 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1005",
                studentId = "stu_rohan_verma",
                studentName = "Rohan Verma",
                studentPhone = "+91 98765 43212",
                hostel = "Tagore Hostel",
                block = "Block B",
                roomNumber = "315",
                title = "Low water pressure in 3rd floor washroom showers",
                description = "Water trickles very slowly during morning rush hours from 7:30 to 9:00 AM.",
                category = ComplaintCategory.WATER_SUPPLY,
                priority = ComplaintPriority.HIGH,
                location = "3rd Floor Common Washroom B-Wing",
                imageUrl = null,
                status = ComplaintStatus.IN_PROGRESS,
                assignedStaffName = "Mahesh Plumber",
                remarks = "Overhead rooftop booster pump pressure regulator needs valve recalibration.",
                createdAt = now - 1 * day - 4 * hour,
                updatedAt = now - 3 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1006",
                studentId = "stu_ananya_singh",
                studentName = "Ananya Singh",
                studentPhone = "+91 98765 43213",
                hostel = "Tagore Hostel",
                block = "Block C",
                roomNumber = "108",
                title = "Corridor window glass cracked due to strong winds",
                description = "Corner pane of the central staircase window is cracked and could fall.",
                category = ComplaintCategory.SECURITY,
                priority = ComplaintPriority.URGENT,
                location = "Block C Staircase between 1st & 2nd floor",
                imageUrl = null,
                status = ComplaintStatus.RECEIVED,
                assignedStaffName = null,
                remarks = "Caution tape placed around stair landing. Glazier summoned.",
                createdAt = now - 3 * hour,
                updatedAt = now - 1 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1007",
                studentId = "stu_karan_jain",
                studentName = "Karan Jain",
                studentPhone = "+91 98765 43214",
                hostel = "Tagore Hostel",
                block = "Block A",
                roomNumber = "210",
                title = "Dustbin not emptied in 2nd floor common balcony",
                description = "Balcony bins overflowing for the past 24 hours.",
                category = ComplaintCategory.CLEANING_HYGIENE,
                priority = ComplaintPriority.MEDIUM,
                location = "Block A, 2nd Floor Balcony",
                imageUrl = null,
                status = ComplaintStatus.CLOSED,
                assignedStaffName = "Sunil (Sanitation Lead)",
                remarks = "Housekeeping supervisor instructed.",
                resolutionNotes = "Area sanitized and new 60L bins installed.",
                isConfirmedByStudent = true,
                studentRating = 5,
                studentFeedback = "Prompt response and clean work. Thank you!",
                createdAt = now - 5 * day,
                updatedAt = now - 2 * day
            ),
            Complaint(
                complaintId = "CMP-2026-1008",
                studentId = "stu_sneha_nair",
                studentName = "Sneha Nair",
                studentPhone = "+91 98765 43215",
                hostel = "Tagore Hostel",
                block = "Block B",
                roomNumber = "112",
                title = "Geyser water heating temperature too low",
                description = "Water remains lukewarm even after keeping geyser switched on for 45 minutes.",
                category = ComplaintCategory.ELECTRICAL,
                priority = ComplaintPriority.MEDIUM,
                location = "Room 112 Bathroom",
                imageUrl = null,
                status = ComplaintStatus.SUBMITTED,
                createdAt = now - 1 * hour,
                updatedAt = now - 1 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1009",
                studentId = "stu_vikram_reddy",
                studentName = "Vikram Reddy",
                studentPhone = "+91 98765 43216",
                hostel = "Tagore Hostel",
                block = "Block C",
                roomNumber = "305",
                title = "Mess dinner served cold on Sunday night",
                description = "Chapatis and dal were cold by 8:45 PM. Requesting hot food warmer maintenance.",
                category = ComplaintCategory.MESS_FOOD,
                priority = ComplaintPriority.LOW,
                location = "Hostel Dining Hall",
                imageUrl = null,
                status = ComplaintStatus.RESOLVED,
                assignedStaffName = "Catering Supervisor",
                remarks = "Bain-marie heating coils serviced.",
                resolutionNotes = "Tested food warmers. Temperature maintaining at 65°C standard.",
                isConfirmedByStudent = false,
                studentRating = 0,
                createdAt = now - 3 * day,
                updatedAt = now - 1 * day
            ),
            Complaint(
                complaintId = "CMP-2026-1010",
                studentId = "stu_priya_sharma",
                studentName = "Priya Sharma",
                studentPhone = "+91 98765 43217",
                hostel = "Tagore Hostel",
                block = "Block B",
                roomNumber = "206",
                title = "Door latch misaligned after rain expansion",
                description = "Wooden door has swelled slightly and latch requires strong push to lock from inside.",
                category = ComplaintCategory.FURNITURE,
                priority = ComplaintPriority.LOW,
                location = "Room 206 Entrance Door",
                imageUrl = null,
                status = ComplaintStatus.IN_PROGRESS,
                assignedStaffName = "Sohan Singh (Carpenter)",
                remarks = "Plane door edges and realign strike plate.",
                createdAt = now - 2 * day,
                updatedAt = now - 7 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1011",
                studentId = "stu_aarav_sharma",
                studentName = "Aarav Sharma",
                studentPhone = "+91 98765 43210",
                hostel = "Tagore Hostel",
                block = "Block B",
                roomNumber = "204",
                title = "Tube light starter blinking repeatedly",
                description = "The fluorescent tube light blinks continuously before turning on.",
                category = ComplaintCategory.ELECTRICAL,
                priority = ComplaintPriority.LOW,
                location = "Room 204 Main Ceiling",
                imageUrl = null,
                status = ComplaintStatus.CLOSED,
                assignedStaffName = "Ramesh Kumar (Electrician)",
                remarks = "Replaced with modern LED batten.",
                resolutionNotes = "Old fluorescent fixture converted to 20W Philips LED batten.",
                isConfirmedByStudent = true,
                studentRating = 5,
                studentFeedback = "Much brighter and silent now. Great job!",
                createdAt = now - 8 * day,
                updatedAt = now - 6 * day
            ),
            Complaint(
                complaintId = "CMP-2026-1012",
                studentId = "stu_diya_patel",
                studentName = "Diya Patel",
                studentPhone = "+91 98765 43211",
                hostel = "Tagore Hostel",
                block = "Block A",
                roomNumber = "102",
                title = "Water cooler on 1st floor dispensing warm water",
                description = "The cooling compressor is not humming.",
                category = ComplaintCategory.WATER_SUPPLY,
                priority = ComplaintPriority.HIGH,
                location = "Block A 1st Floor Water Station",
                imageUrl = null,
                status = ComplaintStatus.ASSIGNED,
                assignedStaffName = "Cooler Tech Services",
                remarks = "Technician dispatched for refrigerant gas check.",
                createdAt = now - 6 * hour,
                updatedAt = now - 3 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1013",
                studentId = "stu_rohan_verma",
                studentName = "Rohan Verma",
                studentPhone = "+91 98765 43212",
                hostel = "Tagore Hostel",
                block = "Block B",
                roomNumber = "315",
                title = "Exhaust fan in common washroom not rotating",
                description = "Ventilation is poor due to stuck exhaust fan motor.",
                category = ComplaintCategory.WASHROOM,
                priority = ComplaintPriority.MEDIUM,
                location = "Block B 3rd Floor Washroom",
                imageUrl = null,
                status = ComplaintStatus.RESOLUTION_UPDATED,
                assignedStaffName = "Ramesh Kumar (Electrician)",
                remarks = "Motor rewinding done and reinstalled.",
                resolutionNotes = "Installed cleaned exhaust fan with safety grill.",
                createdAt = now - 2 * day,
                updatedAt = now - 8 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1014",
                studentId = "stu_karan_jain",
                studentName = "Karan Jain",
                studentPhone = "+91 98765 43214",
                hostel = "Tagore Hostel",
                block = "Block A",
                roomNumber = "210",
                title = "Emergency exit door latch rusty",
                description = "Stairway rear exit push-bar is difficult to depress.",
                category = ComplaintCategory.SECURITY,
                priority = ComplaintPriority.HIGH,
                location = "Rear Emergency Stairwell Block A",
                imageUrl = null,
                status = ComplaintStatus.SUBMITTED,
                createdAt = now - 4 * hour,
                updatedAt = now - 4 * hour
            ),
            Complaint(
                complaintId = "CMP-2026-1015",
                studentId = "stu_ananya_singh",
                studentName = "Ananya Singh",
                studentPhone = "+91 98765 43213",
                hostel = "Tagore Hostel",
                block = "Block C",
                roomNumber = "108",
                title = "Common room ping-pong table net missing bracket",
                description = "Recreation room table tennis net clamp is stripped.",
                category = ComplaintCategory.OTHER,
                priority = ComplaintPriority.LOW,
                location = "Hostel Recreation Room",
                imageUrl = null,
                status = ComplaintStatus.SUBMITTED,
                createdAt = now - 10 * hour,
                updatedAt = now - 10 * hour
            )
        )
        _complaints.value = initialComplaints

        // 8 Sample Announcements
        val initialAnnouncements = listOf(
            Announcement(
                announcementId = "ANN-2026-101",
                title = "Scheduled Water Tank Cleaning & Sanitization",
                description = "The overhead water storage tanks for Blocks A, B, and C will undergo annual chemical disinfection on Saturday, 8th Nov from 10:00 AM to 03:00 PM. Water supply will be suspended during this window. Residents are advised to store drinking water in advance.",
                category = AnnouncementCategory.WATER_SUPPLY,
                priority = AnnouncementPriority.HIGH,
                isPinned = true,
                authorName = "Hostel Administration",
                createdAt = now - 1 * day
            ),
            Announcement(
                announcementId = "ANN-2026-102",
                title = "Hostel Fire Safety Drill & Siren Test",
                description = "Mandatory fire evacuation drill will be conducted on Thursday at 05:00 PM. All students present in the hostel must assemble at the main football ground upon hearing the alarm. Wardens and safety marshals will coordinate floor sweeps.",
                category = AnnouncementCategory.EMERGENCY,
                priority = AnnouncementPriority.URGENT,
                isPinned = true,
                authorName = "Chief Safety Officer & Warden",
                createdAt = now - 2 * day
            ),
            Announcement(
                announcementId = "ANN-2026-103",
                title = "Revised Dining Hall Menu & Sunday Feast Update",
                description = "Following the student mess committee review meeting, South Indian breakfast options have been added on Tuesdays and Thursdays. Paneer Butter Masala and Gulab Jamun will be served for Sunday dinner.",
                category = AnnouncementCategory.MESS_UPDATES,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                authorName = "Mess Secretary",
                createdAt = now - 3 * day
            ),
            Announcement(
                announcementId = "ANN-2026-104",
                title = "High-Speed Wi-Fi Upgrade & Bandwidth Doubling",
                description = "Campus IT has installed 16 new Cisco Wi-Fi 6 access points across corridors in Tagore Hostel. Per-student bandwidth limit has been boosted from 25 Mbps to 60 Mbps. Please report dead zones via DormDesk.",
                category = AnnouncementCategory.MAINTENANCE_UPDATES,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                authorName = "Campus IT Services",
                createdAt = now - 4 * day
            ),
            Announcement(
                announcementId = "ANN-2026-105",
                title = "Hostel Badminton & Table Tennis Tournament 2026",
                description = "Registrations are now open for the Annual Tagore Hostel Indoor Sports Meet. Events include Men's and Women's Singles and Mixed Doubles. Sign up at the common room bulletin by Friday evening.",
                category = AnnouncementCategory.HOSTEL_EVENTS,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                authorName = "Sports Committee",
                createdAt = now - 5 * day
            ),
            Announcement(
                announcementId = "ANN-2026-106",
                title = "Power Substation Preventative Maintenance",
                description = "Grid transformer servicing is scheduled by the state electricity board for Wednesday from 02:00 PM to 04:00 PM. Generator backup will support room lighting, Wi-Fi, and water pumps.",
                category = AnnouncementCategory.ELECTRICITY,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                authorName = "Estate Maintenance Office",
                createdAt = now - 6 * day
            ),
            Announcement(
                announcementId = "ANN-2026-107",
                title = "End-Semester Quiet Hours Policy",
                description = "To foster an optimal study environment for upcoming mid-term examinations, strict silence hours will be enforced across all wings from 10:30 PM to 06:30 AM beginning next Monday.",
                category = AnnouncementCategory.GENERAL_NOTICES,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                authorName = "Dr. Rajesh Mehta (Chief Warden)",
                createdAt = now - 7 * day
            ),
            Announcement(
                announcementId = "ANN-2026-108",
                title = "Lost & Found: Wristwatch and Blue Backpack",
                description = "A silver Titan analog watch and a dark blue Wildcraft backpack found in the 2nd floor study hall have been deposited at Warden Office Room 01. Legitimate owners may claim them with ID proof.",
                category = AnnouncementCategory.GENERAL_NOTICES,
                priority = AnnouncementPriority.NORMAL,
                isPinned = false,
                authorName = "Hostel Caretaker",
                createdAt = now - 8 * day
            )
        )
        _announcements.value = initialAnnouncements

        // 10 Sample Feedbacks
        val initialFeedbacks = listOf(
            FeedbackItem(
                feedbackId = "FB-2026-101",
                studentId = DemoAccounts.SampleStudent.uid,
                studentName = DemoAccounts.SampleStudent.fullName,
                category = FeedbackCategory.MESS_AND_FOOD,
                description = "The new breakfast menu with poha and idli sambar is tasty and fresh! Adding boiled eggs or fruit options on weekends would make it even healthier.",
                rating = 4,
                isAnonymous = false,
                adminResponse = "Thank you Aarav! The mess committee has approved adding seasonal bananas and boiled eggs starting next week.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 3 * day,
                respondedAt = now - 1 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-102",
                studentId = "stu_diya_patel",
                studentName = "Diya Patel",
                category = FeedbackCategory.CLEANLINESS,
                description = "Housekeeping team on Block A 2nd floor is doing a commendable job. The corridors smell fresh and washrooms are scrubbed twice daily.",
                rating = 5,
                isAnonymous = false,
                adminResponse = "Appreciate the kind feedback! We have conveyed your words to sanitation supervisor Sunil.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 4 * day,
                respondedAt = now - 2 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-103",
                studentId = "stu_rohan_verma",
                studentName = "Anonymous",
                category = FeedbackCategory.HOSTEL_FACILITIES,
                description = "Need more ergonomic chairs in the ground floor library study hall. The current plastic chairs cause back fatigue during long study sessions.",
                rating = 3,
                isAnonymous = true,
                adminResponse = "Budget request for 25 mesh high-back study chairs has been submitted to estate management.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 2 * day,
                respondedAt = now - 10 * hour
            ),
            FeedbackItem(
                feedbackId = "FB-2026-104",
                studentId = "stu_karan_jain",
                studentName = "Karan Jain",
                category = FeedbackCategory.STAFF_BEHAVIOUR,
                description = "Night security guards at the south gate are polite and very attentive to student entry logs. Feel very safe.",
                rating = 5,
                isAnonymous = false,
                adminResponse = "Great to hear. Security protocols and student safety remain our topmost priority.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 5 * day,
                respondedAt = now - 3 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-105",
                studentId = "stu_ananya_singh",
                studentName = "Anonymous",
                category = FeedbackCategory.MESS_AND_FOOD,
                description = "Please avoid over-salting the evening dal. Some days it is noticeably too salty for residents.",
                rating = 2,
                isAnonymous = true,
                adminResponse = "Inspected the kitchen spice measurements yesterday. Head chef has calibrated standard seasoning scoops.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 6 * day,
                respondedAt = now - 4 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-106",
                studentId = "stu_sneha_nair",
                studentName = "Sneha Nair",
                category = FeedbackCategory.GENERAL_SUGGESTIONS,
                description = "Can we set up a book donation shelf in the lobby where seniors can leave reference books for juniors?",
                rating = 5,
                isAnonymous = false,
                adminResponse = "Wonderful idea! A wooden bookcase has been allocated beside the reception counter for the book swap.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 7 * day,
                respondedAt = now - 5 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-107",
                studentId = "stu_vikram_reddy",
                studentName = "Vikram Reddy",
                category = FeedbackCategory.HOSTEL_FACILITIES,
                description = "Water cooler on the ground floor dispenses very chilled water. Excellent during hot afternoons.",
                rating = 5,
                isAnonymous = false,
                adminResponse = null,
                isReviewed = true,
                createdAt = now - 1 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-108",
                studentId = "stu_priya_sharma",
                studentName = "Anonymous",
                category = FeedbackCategory.CLEANLINESS,
                description = "Requesting more regular spraying for mosquitoes near the lawn shrubs in the evening.",
                rating = 3,
                isAnonymous = true,
                adminResponse = "Fogging is arranged twice a week (Tuesday & Friday 06:30 PM). Pest control team alerted.",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 2 * day,
                respondedAt = now - 1 * day
            ),
            FeedbackItem(
                feedbackId = "FB-2026-109",
                studentId = DemoAccounts.SampleStudent.uid,
                studentName = DemoAccounts.SampleStudent.fullName,
                category = FeedbackCategory.GENERAL_SUGGESTIONS,
                description = "Having DormDesk has made complaint tracking so smooth and transparent! Huge upgrade from paper registers.",
                rating = 5,
                isAnonymous = false,
                adminResponse = "We are thrilled to serve our residents better with DormDesk. Thank you Aarav!",
                adminRespondedBy = "Dr. Rajesh Mehta",
                isReviewed = true,
                createdAt = now - 1 * day,
                respondedAt = now - 4 * hour
            ),
            FeedbackItem(
                feedbackId = "FB-2026-110",
                studentId = "stu_diya_patel",
                studentName = "Diya Patel",
                category = FeedbackCategory.STAFF_BEHAVIOUR,
                description = "Carpenter Sohan Singh was punctual, courteous, and cleaned up the wood dust after fixing the desk latch.",
                rating = 5,
                isAnonymous = false,
                adminResponse = null,
                isReviewed = false,
                createdAt = now - 8 * hour
            )
        )
        _feedbacks.value = initialFeedbacks

        // Sample Notifications
        val initialNotifications = listOf(
            NotificationItem(
                notificationId = "NOTIF-101",
                userId = DemoAccounts.SampleStudent.uid,
                title = "Complaint Work in Progress",
                message = "Electrician Ramesh Kumar has begun repairs on ticket CMP-2026-1001 (Ceiling fan noise).",
                type = NotificationType.COMPLAINT_STATUS_CHANGED,
                relatedEntityId = "CMP-2026-1001",
                isRead = false,
                createdAt = now - 4 * hour
            ),
            NotificationItem(
                notificationId = "NOTIF-102",
                userId = DemoAccounts.SampleStudent.uid,
                title = "Resolution Ready for Verification",
                message = "Complaint CMP-2026-1003 (Drawer lock broken) was marked resolved. Please verify and submit your rating.",
                type = NotificationType.RESOLUTION_UPDATED,
                relatedEntityId = "CMP-2026-1003",
                isRead = false,
                createdAt = now - 6 * hour
            ),
            NotificationItem(
                notificationId = "NOTIF-103",
                userId = DemoAccounts.SampleStudent.uid,
                title = "New Hostel Announcement",
                message = "Scheduled Water Tank Cleaning & Sanitization on Saturday 10:00 AM – 03:00 PM.",
                type = NotificationType.NEW_ANNOUNCEMENT,
                relatedEntityId = "ANN-2026-101",
                isRead = true,
                createdAt = now - 1 * day
            ),
            NotificationItem(
                notificationId = "NOTIF-104",
                userId = DemoAccounts.SampleStudent.uid,
                title = "Warden Responded to Feedback",
                message = "Dr. Rajesh Mehta responded to your mess food feedback: 'Approved adding seasonal bananas...'",
                type = NotificationType.FEEDBACK_RESPONSE,
                relatedEntityId = "FB-2026-101",
                isRead = true,
                createdAt = now - 1 * day - 2 * hour
            )
        )
        _notifications.value = initialNotifications

        // Welcome Chat Message
        _chatMessages.value = listOf(
            ChatMessage(
                messageId = "MSG-INIT",
                userId = DemoAccounts.SampleStudent.uid,
                message = "Hello! I am **DormDesk AI**, your 24/7 hostel resident assistant. Ask me anything about your active complaints, warden office hours, hostel rules, or file new requests.",
                senderType = SenderType.ASSISTANT,
                timestamp = now,
                suggestedActions = listOf(
                    "What is my complaint status?",
                    "How do I raise a complaint?",
                    "What are the latest hostel announcements?",
                    "How can I contact the warden?",
                    "How do I submit feedback?"
                )
            )
        )
    }

    override suspend fun login(email: String, role: UserRole): Result<User> {
        val user = if (role == UserRole.WARDEN) {
            DemoAccounts.AllDemoWardens.find { it.email.equals(email, ignoreCase = true) }
                ?: DemoAccounts.SampleWarden
        } else {
            DemoAccounts.AllDemoStudents.find { it.email.equals(email, ignoreCase = true) }
                ?: DemoAccounts.SampleStudent
        }
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun registerStudent(user: User): Result<User> {
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun logout() {
        _currentUser.value = null
    }

    override suspend fun deleteAccount(userId: String): Result<Unit> = runCatching {
        // Remove all user data
        _complaints.update { list -> list.filter { it.studentId != userId } }
        _feedbacks.update { list -> list.filter { it.studentId != userId } }
        _notifications.update { emptyList() }
        _chatMessages.update { emptyList() }
        _currentUser.value = null
        // In production: FirebaseAuth.getInstance().currentUser?.delete()?.await()
        // Also delete from Realtime Database and Storage
    }

    override fun switchUser(user: User) {
        _currentUser.value = user
    }

    override suspend fun createComplaint(
        title: String,
        category: ComplaintCategory,
        description: String,
        priority: ComplaintPriority,
        location: String,
        imageUrl: String?
    ): Result<Complaint> {
        val user = _currentUser.value ?: DemoAccounts.SampleStudent
        val newId = IdGenerator.generateComplaintId()
        val now = System.currentTimeMillis()

        val newComplaint = Complaint(
            complaintId = newId,
            studentId = user.uid,
            studentName = user.fullName,
            studentPhone = user.phone,
            hostel = user.hostel,
            block = user.block,
            roomNumber = user.roomNumber,
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            location = location.trim(),
            imageUrl = imageUrl,
            status = ComplaintStatus.SUBMITTED,
            createdAt = now,
            updatedAt = now,
            activityLogs = listOf(
                ActivityLogEntry(
                    id = "log-" + IdGenerator.generateNotificationId(),
                    timestamp = now,
                    action = "Complaint Submitted",
                    actorRole = "Student",
                    actorName = user.fullName,
                    note = "Complaint registered via DormDesk mobile application."
                )
            )
        )

        _complaints.update { listOf(newComplaint) + it }

        // Generate student notification
        val notif = NotificationItem(
            notificationId = IdGenerator.generateNotificationId(),
            userId = user.uid,
            title = "Complaint Registered: $newId",
            message = "Your complaint for '${title.take(30)}...' has been submitted to the warden.",
            type = NotificationType.COMPLAINT_REGISTERED,
            relatedEntityId = newId,
            isRead = false,
            createdAt = now
        )
        _notifications.update { listOf(notif) + it }

        return Result.success(newComplaint)
    }

    override suspend fun getComplaintById(complaintId: String): Complaint? {
        return _complaints.value.find { it.complaintId == complaintId }
    }

    override suspend fun updateComplaintStatus(
        complaintId: String,
        newStatus: ComplaintStatus,
        assignedStaffName: String?,
        remarks: String?,
        resolutionNotes: String?,
        completionPhotoUrl: String?
    ): Result<Complaint> {
        var updatedItem: Complaint? = null
        val now = System.currentTimeMillis()
        val warden = _currentUser.value ?: DemoAccounts.SampleWarden

        _complaints.update { list ->
            list.map { item ->
                if (item.complaintId == complaintId) {
                    val logs = item.activityLogs.toMutableList()
                    val noteBuilder = mutableListOf<String>()
                    if (!assignedStaffName.isNullOrBlank() && assignedStaffName != item.assignedStaffName) {
                        noteBuilder.add("Staff Assigned: $assignedStaffName")
                    }
                    if (!remarks.isNullOrBlank()) {
                        noteBuilder.add("Remarks: $remarks")
                    }
                    if (!resolutionNotes.isNullOrBlank()) {
                        noteBuilder.add("Resolution: $resolutionNotes")
                    }

                    logs.add(
                        ActivityLogEntry(
                            id = "log-" + IdGenerator.generateNotificationId(),
                            timestamp = now,
                            action = "Status changed to ${newStatus.label}",
                            actorRole = warden.role.displayName,
                            actorName = warden.fullName,
                            note = if (noteBuilder.isEmpty()) "Status updated." else noteBuilder.joinToString(" | ")
                        )
                    )

                    val updated = item.copy(
                        status = newStatus,
                        assignedStaffName = assignedStaffName ?: item.assignedStaffName,
                        remarks = remarks ?: item.remarks,
                        resolutionNotes = resolutionNotes ?: item.resolutionNotes,
                        resolutionImageUrl = completionPhotoUrl ?: item.resolutionImageUrl,
                        updatedAt = now,
                        activityLogs = logs
                    )
                    updatedItem = updated
                    updated
                } else {
                    item
                }
            }
        }

        updatedItem?.let { comp ->
            // Notify student of status update
            val notif = NotificationItem(
                notificationId = IdGenerator.generateNotificationId(),
                userId = comp.studentId,
                title = "Complaint Status: ${newStatus.label}",
                message = "Your ticket ${comp.complaintId} was updated to '${newStatus.label}'.",
                type = if (newStatus == ComplaintStatus.RESOLVED) NotificationType.RESOLUTION_UPDATED else NotificationType.COMPLAINT_STATUS_CHANGED,
                relatedEntityId = comp.complaintId,
                isRead = false,
                createdAt = now
            )
            _notifications.update { listOf(notif) + it }
            return Result.success(comp)
        }

        return Result.failure(Exception("Complaint not found"))
    }

    override suspend fun confirmComplaintResolution(
        complaintId: String,
        rating: Int,
        feedback: String?
    ): Result<Complaint> {
        var updatedItem: Complaint? = null
        val now = System.currentTimeMillis()
        val student = _currentUser.value ?: DemoAccounts.SampleStudent

        _complaints.update { list ->
            list.map { item ->
                if (item.complaintId == complaintId) {
                    val logs = item.activityLogs.toMutableList()
                    logs.add(
                        ActivityLogEntry(
                            id = "log-" + IdGenerator.generateNotificationId(),
                            timestamp = now,
                            action = "Resolution Confirmed by Resident",
                            actorRole = "Student",
                            actorName = student.fullName,
                            note = "Rated $rating/5 stars. Feedback: ${feedback ?: "None provided"}"
                        )
                    )
                    val updated = item.copy(
                        status = ComplaintStatus.CLOSED,
                        isConfirmedByStudent = true,
                        studentRating = rating,
                        studentFeedback = feedback,
                        updatedAt = now,
                        activityLogs = logs
                    )
                    updatedItem = updated
                    updated
                } else {
                    item
                }
            }
        }

        updatedItem?.let {
            return Result.success(it)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    override suspend fun reopenComplaint(complaintId: String, reason: String): Result<Complaint> {
        var updatedItem: Complaint? = null
        val now = System.currentTimeMillis()
        val student = _currentUser.value ?: DemoAccounts.SampleStudent

        _complaints.update { list ->
            list.map { item ->
                if (item.complaintId == complaintId) {
                    val logs = item.activityLogs.toMutableList()
                    logs.add(
                        ActivityLogEntry(
                            id = "log-" + IdGenerator.generateNotificationId(),
                            timestamp = now,
                            action = "Ticket Reopened by Resident",
                            actorRole = "Student",
                            actorName = student.fullName,
                            note = "Reopen justification: $reason"
                        )
                    )
                    val updated = item.copy(
                        status = ComplaintStatus.RECEIVED,
                        reopenReason = reason,
                        updatedAt = now,
                        activityLogs = logs
                    )
                    updatedItem = updated
                    updated
                } else {
                    item
                }
            }
        }

        updatedItem?.let {
            return Result.success(it)
        }
        return Result.failure(Exception("Complaint not found"))
    }

    override suspend fun submitFeedback(
        category: FeedbackCategory,
        description: String,
        rating: Int,
        isAnonymous: Boolean
    ): Result<FeedbackItem> {
        val user = _currentUser.value ?: DemoAccounts.SampleStudent
        val newId = IdGenerator.generateFeedbackId()
        val now = System.currentTimeMillis()

        val item = FeedbackItem(
            feedbackId = newId,
            studentId = user.uid,
            studentName = if (isAnonymous) "Anonymous Resident" else user.fullName,
            category = category,
            description = description.trim(),
            rating = rating,
            isAnonymous = isAnonymous,
            adminResponse = null,
            adminRespondedBy = null,
            isReviewed = false,
            createdAt = now
        )

        _feedbacks.update { listOf(item) + it }
        return Result.success(item)
    }

    override suspend fun respondToFeedback(
        feedbackId: String,
        adminResponse: String
    ): Result<FeedbackItem> {
        var updatedItem: FeedbackItem? = null
        val now = System.currentTimeMillis()
        val warden = _currentUser.value ?: DemoAccounts.SampleWarden

        _feedbacks.update { list ->
            list.map { item ->
                if (item.feedbackId == feedbackId) {
                    val updated = item.copy(
                        adminResponse = adminResponse.trim(),
                        adminRespondedBy = warden.fullName,
                        isReviewed = true,
                        respondedAt = now
                    )
                    updatedItem = updated
                    updated
                } else {
                    item
                }
            }
        }

        updatedItem?.let { fb ->
            // Notify the student if not anonymous
            if (!fb.isAnonymous) {
                val notif = NotificationItem(
                    notificationId = IdGenerator.generateNotificationId(),
                    userId = fb.studentId,
                    title = "Warden Responded to Feedback",
                    message = "${warden.fullName} replied to your feedback under ${fb.category.label}.",
                    type = NotificationType.FEEDBACK_RESPONSE,
                    relatedEntityId = fb.feedbackId,
                    isRead = false,
                    createdAt = now
                )
                _notifications.update { listOf(notif) + it }
            }
            return Result.success(fb)
        }
        return Result.failure(Exception("Feedback not found"))
    }

    override suspend fun createAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean
    ): Result<Announcement> {
        val warden = _currentUser.value ?: DemoAccounts.SampleWarden
        val newId = IdGenerator.generateAnnouncementId()
        val now = System.currentTimeMillis()

        val newAnn = Announcement(
            announcementId = newId,
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            isPinned = isPinned,
            authorName = warden.fullName,
            createdAt = now,
            isRead = false
        )

        _announcements.update { list ->
            if (isPinned) listOf(newAnn) + list else list + newAnn
        }

        // Notify all residents
        val notif = NotificationItem(
            notificationId = IdGenerator.generateNotificationId(),
            userId = "all",
            title = "New Notice: $title",
            message = description.take(80) + if (description.length > 80) "..." else "",
            type = NotificationType.NEW_ANNOUNCEMENT,
            relatedEntityId = newId,
            isRead = false,
            createdAt = now
        )
        _notifications.update { listOf(notif) + it }

        return Result.success(newAnn)
    }

    override suspend fun updateAnnouncement(
        announcementId: String,
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean
    ): Result<Announcement> {
        var updatedItem: Announcement? = null
        _announcements.update { list ->
            list.map { item ->
                if (item.announcementId == announcementId) {
                    val updated = item.copy(
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        priority = priority,
                        isPinned = isPinned
                    )
                    updatedItem = updated
                    updated
                } else {
                    item
                }
            }
        }
        updatedItem?.let { return Result.success(it) }
        return Result.failure(Exception("Announcement not found"))
    }

    override suspend fun deleteAnnouncement(announcementId: String): Result<Boolean> {
        _announcements.update { list -> list.filterNot { it.announcementId == announcementId } }
        return Result.success(true)
    }

    override suspend fun markAnnouncementAsRead(announcementId: String) {
        _announcements.update { list ->
            list.map { if (it.announcementId == announcementId) it.copy(isRead = true) else it }
        }
    }

    override suspend fun markNotificationAsRead(notificationId: String) {
        _notifications.update { list ->
            list.map { if (it.notificationId == notificationId) it.copy(isRead = true) else it }
        }
    }

    override suspend fun markAllNotificationsAsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    override suspend fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    override suspend fun addChatMessage(message: ChatMessage) {
        _chatMessages.update { it + message }
    }

    override suspend fun clearChatHistory() {
        _chatMessages.value = emptyList()
    }
}
