package com.dormdesk.app.data.repository

import com.dormdesk.app.data.model.*

object DemoDataProvider {

    // 3 Wardens / Admins
    val wardenUser1 = User(
        uid = "warden_01",
        fullName = "Rakesh Mehta",
        email = "warden@dormdesk.app",
        studentId = "STAFF-W01",
        phone = "+91 98765 43210",
        hostel = "Aryabhatta Hostel",
        block = "Admin Block",
        roomNumber = "G-02",
        role = UserRole.WARDEN
    )

    val wardenUser2 = User(
        uid = "warden_02",
        fullName = "Dr. Rajeshwar Sharma",
        email = "rajeshwar.warden@dormdesk.app",
        studentId = "STAFF-W02",
        phone = "+91 98765 43211",
        hostel = "Aryabhatta Hostel",
        block = "Admin Block",
        roomNumber = "G-03",
        role = UserRole.WARDEN
    )

    val wardenUser3 = User(
        uid = "warden_03",
        fullName = "Prof. Ananya Sen",
        email = "ananya.warden@dormdesk.app",
        studentId = "STAFF-W03",
        phone = "+91 98765 43212",
        hostel = "Aryabhatta Hostel",
        block = "Admin Block",
        roomNumber = "G-04",
        role = UserRole.WARDEN
    )

    // 8 Students
    val studentUser1 = User(
        uid = "student_01",
        fullName = "Aarav Patel",
        email = "student@dormdesk.app",
        studentId = "2024CS101",
        phone = "+91 91234 56780",
        hostel = "Aryabhatta Hostel",
        block = "Block B",
        roomNumber = "204",
        role = UserRole.STUDENT
    )

    val studentUser2 = User(
        uid = "student_02",
        fullName = "Rhea Mukherjee",
        email = "rhea.m@dormdesk.app",
        studentId = "2024CS102",
        phone = "+91 91234 56781",
        hostel = "Aryabhatta Hostel",
        block = "Block A",
        roomNumber = "108",
        role = UserRole.STUDENT
    )

    val studentUser3 = User(
        uid = "student_03",
        fullName = "Kabir Deshmukh",
        email = "kabir.d@dormdesk.app",
        studentId = "2024EE205",
        phone = "+91 91234 56782",
        hostel = "Aryabhatta Hostel",
        block = "Block B",
        roomNumber = "312",
        role = UserRole.STUDENT
    )

    val studentUser4 = User(
        uid = "student_04",
        fullName = "Meera Nair",
        email = "meera.n@dormdesk.app",
        studentId = "2024ME114",
        phone = "+91 91234 56783",
        hostel = "Aryabhatta Hostel",
        block = "Block C",
        roomNumber = "215",
        role = UserRole.STUDENT
    )

    val studentUser5 = User(
        uid = "student_05",
        fullName = "Siddharth Verma",
        email = "sid.v@dormdesk.app",
        studentId = "2024CV088",
        phone = "+91 91234 56784",
        hostel = "Aryabhatta Hostel",
        block = "Block A",
        roomNumber = "301",
        role = UserRole.STUDENT
    )

    val studentUser6 = User(
        uid = "student_06",
        fullName = "Diya Kulkarni",
        email = "diya.k@dormdesk.app",
        studentId = "2024IT142",
        phone = "+91 91234 56785",
        hostel = "Aryabhatta Hostel",
        block = "Block C",
        roomNumber = "104",
        role = UserRole.STUDENT
    )

    val studentUser7 = User(
        uid = "student_07",
        fullName = "Arjun Singhania",
        email = "arjun.s@dormdesk.app",
        studentId = "2024EC166",
        phone = "+91 91234 56786",
        hostel = "Aryabhatta Hostel",
        block = "Block B",
        roomNumber = "118",
        role = UserRole.STUDENT
    )

    val studentUser8 = User(
        uid = "student_08",
        fullName = "Tanvi Bansal",
        email = "tanvi.b@dormdesk.app",
        studentId = "2024CH092",
        phone = "+91 91234 56787",
        hostel = "Aryabhatta Hostel",
        block = "Block A",
        roomNumber = "222",
        role = UserRole.STUDENT
    )

    val allUsers = listOf(
        wardenUser1, wardenUser2, wardenUser3,
        studentUser1, studentUser2, studentUser3, studentUser4,
        studentUser5, studentUser6, studentUser7, studentUser8
    )

    // Current time offsets for realistic timestamps
    private val now = System.currentTimeMillis()
    private val hour = 3600 * 1000L
    private val day = 24 * hour

    // 15 Sample Complaints across categories and all 7 status stages
    fun getInitialComplaints(): List<Complaint> = listOf(
        Complaint(
            complaintId = "DD-1001",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "Ceiling fan making vibrating grinding noise",
            description = "The bedroom ceiling fan rattles loudly on speed 3 and 4, making it impossible to study or sleep at night.",
            category = ComplaintCategory.ELECTRICAL,
            priority = ComplaintPriority.MEDIUM,
            location = "Room 204, Block B",
            imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=400&q=80",
            status = ComplaintStatus.SUBMITTED,
            assignedStaffName = "",
            remarks = "Pending warden initial review.",
            createdAt = now - 2 * hour,
            updatedAt = now - 2 * hour
        ),
        Complaint(
            complaintId = "DD-1002",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "Washroom flush valve stuck and leaking water",
            description = "Flush tank mechanism remains open continuously, leading to continuous water overflow on bathroom tiles.",
            category = ComplaintCategory.PLUMBING,
            priority = ComplaintPriority.HIGH,
            location = "Attached Washroom, Room 204",
            imageUrl = "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?auto=format&fit=crop&w=400&q=80",
            status = ComplaintStatus.RECEIVED,
            assignedStaffName = "",
            remarks = "Acknowledged by Warden Office. Plumber dispatch scheduled for afternoon shift.",
            createdAt = now - 14 * hour,
            updatedAt = now - 6 * hour
        ),
        Complaint(
            complaintId = "DD-1003",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "Study table drawer lock broken",
            description = "The wooden drawer runner has cracked and the internal lock does not latch properly.",
            category = ComplaintCategory.FURNITURE,
            priority = ComplaintPriority.LOW,
            location = "Desk 1, Room 204",
            imageUrl = null,
            status = ComplaintStatus.ASSIGNED,
            assignedStaffName = "Ramesh Kumar (Carpenter)",
            remarks = "Carpentry job order #J-88 issued.",
            createdAt = now - 1 * day,
            updatedAt = now - 10 * hour
        ),
        Complaint(
            complaintId = "DD-1004",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "LAN ethernet port dead and loose contact",
            description = "Wall RJ45 socket does not establish 1Gbps link; pins appear bent inside the wall plate.",
            category = ComplaintCategory.INTERNET_WIFI,
            priority = ComplaintPriority.MEDIUM,
            location = "Port B, Room 204",
            imageUrl = null,
            status = ComplaintStatus.IN_PROGRESS,
            assignedStaffName = "Sunil Verma (Network Tech)",
            remarks = "Network cable tester showed open pair on pin 3. Replacing keystone jack today.",
            createdAt = now - 2 * day,
            updatedAt = now - 4 * hour
        ),
        Complaint(
            complaintId = "DD-1005",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "Balcony drain blocked causing waterlogging",
            description = "Rainwater collected on the balcony does not drain out due to debris buildup in drain trap.",
            category = ComplaintCategory.CLEANING_HYGIENE,
            priority = ComplaintPriority.HIGH,
            location = "Balcony, Room 204",
            imageUrl = null,
            status = ComplaintStatus.RESOLUTION_UPDATED,
            assignedStaffName = "Mohan Lal (Sanitation)",
            remarks = "Deep cleaning completed. Drain trap cleared.",
            resolutionNotes = "Drain pipe cleared using plumbing snake. Water flow verified normal.",
            completionImageUrl = "https://images.unsplash.com/photo-1584820927498-cfe5211fd8bf?auto=format&fit=crop&w=400&q=80",
            createdAt = now - 3 * day,
            updatedAt = now - 3 * hour
        ),
        Complaint(
            complaintId = "DD-1006",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "Main tube light flickering repeatedly",
            description = "20W LED batten lamp driver started flickering every 5 seconds.",
            category = ComplaintCategory.ELECTRICAL,
            priority = ComplaintPriority.LOW,
            location = "Main Room 204",
            imageUrl = null,
            status = ComplaintStatus.RESOLVED,
            assignedStaffName = "Prakash (Electrician)",
            remarks = "Replacement LED tube installed.",
            resolutionNotes = "Defective batten replaced with Philips 20W Cool Day Light.",
            createdAt = now - 5 * day,
            updatedAt = now - 1 * day
        ),
        Complaint(
            complaintId = "DD-1007",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            roomNumber = studentUser1.roomNumber,
            title = "Corridor window latch rusted and jammed",
            description = "Window latch was stuck shut and unable to open for ventilation.",
            category = ComplaintCategory.OTHER,
            priority = ComplaintPriority.LOW,
            location = "Outside Room 204 Corridor",
            imageUrl = null,
            status = ComplaintStatus.CLOSED,
            assignedStaffName = "Ramesh Kumar (Carpenter)",
            remarks = "Lubricated and new brass screws attached.",
            resolutionNotes = "Latch replaced and lubricated with WD-40.",
            createdAt = now - 10 * day,
            updatedAt = now - 6 * day,
            studentRating = 5,
            studentConfirmed = true
        ),
        Complaint(
            complaintId = "DD-1008",
            studentId = studentUser2.uid,
            studentName = studentUser2.fullName,
            roomNumber = studentUser2.roomNumber,
            title = "Exposed electrical socket near washbasin",
            description = "Socket faceplate is cracked with live wires visible near wet counter. Highly dangerous.",
            category = ComplaintCategory.SECURITY,
            priority = ComplaintPriority.URGENT,
            location = "Room 108 Wash Area",
            imageUrl = "https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?auto=format&fit=crop&w=400&q=80",
            status = ComplaintStatus.ASSIGNED,
            assignedStaffName = "Prakash (Electrician)",
            remarks = "Urgent high priority hazard. Technician dispatched with replacement modular box.",
            createdAt = now - 5 * hour,
            updatedAt = now - 3 * hour
        ),
        Complaint(
            complaintId = "DD-1009",
            studentId = studentUser3.uid,
            studentName = studentUser3.fullName,
            roomNumber = studentUser3.roomNumber,
            title = "Geyser not heating water in 3rd floor bathroom",
            description = "Water remains cold after 25 minutes of heating; indicator lamp does not turn red.",
            category = ComplaintCategory.WATER_SUPPLY,
            priority = ComplaintPriority.HIGH,
            location = "Common Washroom 3F",
            imageUrl = null,
            status = ComplaintStatus.IN_PROGRESS,
            assignedStaffName = "Dinesh Sharma (HVAC & Geyser)",
            remarks = "Heating element burnt out due to hard water scaling. Ordering 2kW copper element.",
            createdAt = now - 1 * day,
            updatedAt = now - 5 * hour
        ),
        Complaint(
            complaintId = "DD-1010",
            studentId = studentUser4.uid,
            studentName = studentUser4.fullName,
            roomNumber = studentUser4.roomNumber,
            title = "Floor water cooler dispensing lukewarm water",
            description = "Water cooler compressor is not tripping on; cooling coil has zero frost.",
            category = ComplaintCategory.WATER_SUPPLY,
            priority = ComplaintPriority.MEDIUM,
            location = "Block C, 2nd Floor Lobby",
            imageUrl = null,
            status = ComplaintStatus.RECEIVED,
            assignedStaffName = "",
            remarks = "Scheduled with refrigeration contract team.",
            createdAt = now - 1 * day,
            updatedAt = now - 12 * hour
        ),
        Complaint(
            complaintId = "DD-1011",
            studentId = studentUser5.uid,
            studentName = studentUser5.fullName,
            roomNumber = studentUser5.roomNumber,
            title = "Food quality complaint: Undercooked rice and sour curd",
            description = "During Thursday dinner, rice was partially raw and curd smelled fermented/sour.",
            category = ComplaintCategory.MESS_FOOD,
            priority = ComplaintPriority.HIGH,
            location = "Central Mess Dining Hall",
            imageUrl = null,
            status = ComplaintStatus.RESOLUTION_UPDATED,
            assignedStaffName = "Mess Contractor Inspection Team",
            remarks = "Notice served to mess contractor. Curd batch supplier changed.",
            resolutionNotes = "Warden visited mess storehouse. Raw stock quarantined and fresh batch provided.",
            createdAt = now - 2 * day,
            updatedAt = now - 8 * hour
        ),
        Complaint(
            complaintId = "DD-1012",
            studentId = studentUser6.uid,
            studentName = studentUser6.fullName,
            roomNumber = studentUser6.roomNumber,
            title = "Wi-Fi access point reboot loop on 1st Floor Block C",
            description = "Access point LED keeps alternating between amber and green; devices disconnect every 30 seconds.",
            category = ComplaintCategory.INTERNET_WIFI,
            priority = ComplaintPriority.HIGH,
            location = "Block C Corridor 1F",
            imageUrl = null,
            status = ComplaintStatus.SUBMITTED,
            assignedStaffName = "",
            remarks = "Reported by resident.",
            createdAt = now - 3 * hour,
            updatedAt = now - 3 * hour
        ),
        Complaint(
            complaintId = "DD-1013",
            studentId = studentUser7.uid,
            studentName = studentUser7.fullName,
            roomNumber = studentUser7.roomNumber,
            title = "Main door lock cylinder slipping with key",
            description = "Godrej mortise lock brass cylinder spins without catching the deadbolt.",
            category = ComplaintCategory.SECURITY,
            priority = ComplaintPriority.URGENT,
            location = "Room 118 Main Entrance",
            imageUrl = null,
            status = ComplaintStatus.RESOLVED,
            assignedStaffName = "Ramesh Kumar (Carpenter)",
            remarks = "Lock cylinder replaced with brand new set + 3 keys handed to student.",
            resolutionNotes = "Replaced 60mm lock cylinder. Student inspected and verified locking mechanism.",
            createdAt = now - 4 * day,
            updatedAt = now - 2 * day
        ),
        Complaint(
            complaintId = "DD-1014",
            studentId = studentUser8.uid,
            studentName = studentUser8.fullName,
            roomNumber = studentUser8.roomNumber,
            title = "Bed frame iron angle bent and unstable",
            description = "Middle support leg of metal cot bent inwards; bed slopes towards the left.",
            category = ComplaintCategory.FURNITURE,
            priority = ComplaintPriority.MEDIUM,
            location = "Room 222, Bed 2",
            imageUrl = null,
            status = ComplaintStatus.CLOSED,
            assignedStaffName = "Welding & Fabrication Crew",
            remarks = "Welded support angle plate.",
            resolutionNotes = "Structural reinforcement welded and rubber feet fitted.",
            createdAt = now - 8 * day,
            updatedAt = now - 5 * day,
            studentRating = 4,
            studentConfirmed = true
        ),
        Complaint(
            complaintId = "DD-1015",
            studentId = studentUser3.uid,
            studentName = studentUser3.fullName,
            roomNumber = studentUser3.roomNumber,
            title = "Staircase emergency light battery dead",
            description = "Automatic emergency backup light does not trigger during grid power cut in Block B staircase.",
            category = ComplaintCategory.ELECTRICAL,
            priority = ComplaintPriority.URGENT,
            location = "Block B Stairwell Between 2F & 3F",
            imageUrl = null,
            status = ComplaintStatus.SUBMITTED,
            assignedStaffName = "",
            remarks = "Logged for safety inspection.",
            createdAt = now - 1 * hour,
            updatedAt = now - 1 * hour
        )
    )

    // 8 Sample Announcements
    fun getInitialAnnouncements(): List<Announcement> = listOf(
        Announcement(
            announcementId = "ANN-201",
            title = "Emergency Water Tank Cleaning & Supply Schedule",
            description = "Overhead rooftop tanks will undergo mandatory chlorination on Saturday from 10:00 AM to 4:00 PM. Tap water supply will be suspended during this interval. Residents are advised to store sufficient water for daily use.",
            category = AnnouncementCategory.WATER_SUPPLY,
            priority = ComplaintPriority.URGENT,
            createdAt = now - 4 * hour,
            isPinned = true,
            createdBy = "Hostel Warden Office",
            isRead = false
        ),
        Announcement(
            announcementId = "ANN-202",
            title = "Special Diwali Weekend Mess Feast & Timings",
            description = "Special festive dinner will be served on Sunday from 7:00 PM to 10:00 PM featuring paneer butter masala, gulab jamun, and special pulav. Mess card entry will be validated at reception.",
            category = AnnouncementCategory.MESS_UPDATES,
            priority = ComplaintPriority.LOW,
            createdAt = now - 1 * day,
            isPinned = true,
            createdBy = "Chief Warden & Mess Committee",
            isRead = false
        ),
        Announcement(
            announcementId = "ANN-203",
            title = "Campus High-Speed Wi-Fi Upgrade & Maintenance Window",
            description = "IT Infrastructure Services will upgrade core switches on Tuesday between 1:00 AM and 4:00 AM. Intermittent packet drops or disconnection may occur on 5GHz SSIDs during this maintenance.",
            category = AnnouncementCategory.MAINTENANCE_UPDATES,
            priority = ComplaintPriority.MEDIUM,
            createdAt = now - 2 * day,
            isPinned = false,
            createdBy = "Campus IT Cell",
            isRead = true
        ),
        Announcement(
            announcementId = "ANN-204",
            title = "Hostel Night Interschool Badminton Tournament Registration",
            description = "Registrations are now open for singles and doubles badminton tournament scheduled next Friday at the Indoor Sports Complex. Attractive trophies and certificates for winners.",
            category = AnnouncementCategory.HOSTEL_EVENTS,
            priority = ComplaintPriority.LOW,
            createdAt = now - 3 * day,
            isPinned = false,
            createdBy = "Hostel Sports Council",
            isRead = true
        ),
        Announcement(
            announcementId = "ANN-205",
            title = "DG Genset Switchgear Testing on Thursday",
            description = "Backup diesel generator automatic transfer switch (ATS) testing will occur between 3:00 PM and 3:30 PM. Elevators will temporarily halt during the switch test.",
            category = AnnouncementCategory.ELECTRICITY,
            priority = ComplaintPriority.MEDIUM,
            createdAt = now - 4 * day,
            isPinned = false,
            createdBy = "Estate Electrical Section",
            isRead = true
        ),
        Announcement(
            announcementId = "ANN-206",
            title = "Strict Curfew Timings & Night Out Slip Protocol",
            description = "All residents must return inside hostel premises before 10:00 PM. Any night-out require pre-authorized digital approval through the parent portal by 6:00 PM.",
            category = AnnouncementCategory.EMERGENCY_ANNOUNCEMENTS,
            priority = ComplaintPriority.HIGH,
            createdAt = now - 6 * day,
            isPinned = false,
            createdBy = "Office of the Proctor & Warden",
            isRead = true
        ),
        Announcement(
            announcementId = "ANN-207",
            title = "Semester Room Inventory & Maintenance Verification",
            description = "Warden inspection teams will conduct room furniture and electrical audit next week. Students are requested to keep study tables and corridors unblocked.",
            category = AnnouncementCategory.GENERAL_NOTICES,
            priority = ComplaintPriority.LOW,
            createdAt = now - 8 * day,
            isPinned = false,
            createdBy = "Hostel Administrative Wing",
            isRead = true
        ),
        Announcement(
            announcementId = "ANN-208",
            title = "Installation of Sanitary Vending & Disposal Units in Block A & C",
            description = "Automatic hygiene dispensers have been installed in common washrooms. For issues or refill requests, report via DormDesk Cleanliness category.",
            category = AnnouncementCategory.GENERAL_NOTICES,
            priority = ComplaintPriority.LOW,
            createdAt = now - 10 * day,
            isPinned = false,
            createdBy = "Hostel Welfare Committee",
            isRead = true
        )
    )

    // 10 Sample Feedback Submissions
    fun getInitialFeedbacks(): List<Feedback> = listOf(
        Feedback(
            feedbackId = "FB-501",
            studentId = studentUser1.uid,
            studentName = studentUser1.fullName,
            category = FeedbackCategory.HOSTEL_FACILITIES,
            description = "The newly installed air conditioner in the common reading hall makes a huge difference during exam weeks. Please consider extending reading hall hours to 2:00 AM.",
            rating = 5,
            isAnonymous = false,
            adminResponse = "Thank you Aarav! During mid-term and end-term weeks, the reading hall will operate until 2:30 AM with security presence.",
            createdAt = now - 1 * day,
            isReviewed = true
        ),
        Feedback(
            feedbackId = "FB-502",
            studentId = studentUser2.uid,
            studentName = studentUser2.fullName,
            category = FeedbackCategory.MESS_FOOD,
            description = "Breakfast idli and sambar on Wednesdays has been consistently cold by 8:45 AM. The warmers seem turned down too low.",
            rating = 3,
            isAnonymous = false,
            adminResponse = "Mess supervisor instructed to keep chafing fuel burners lit until 9:45 AM.",
            createdAt = now - 2 * day,
            isReviewed = true
        ),
        Feedback(
            feedbackId = "FB-503",
            studentId = "anon_01",
            studentName = "Anonymous",
            category = FeedbackCategory.STAFF_BEHAVIOUR,
            description = "The night shift security guard at Gate 2 was polite and helped carry luggage during the heavy rain. Commendable service.",
            rating = 5,
            isAnonymous = true,
            adminResponse = "Appreciation recorded in the security personnel logbook.",
            createdAt = now - 3 * day,
            isReviewed = true
        ),
        Feedback(
            feedbackId = "FB-504",
            studentId = studentUser3.uid,
            studentName = studentUser3.fullName,
            category = FeedbackCategory.CLEANLINESS,
            description = "Corridor dustbins on Block B 3rd floor are overflowing by 6 PM. Emptying them twice a day instead of once would help.",
            rating = 2,
            isAnonymous = false,
            adminResponse = "Sanitation supervisor has adjusted cleaning shift to include a 5:00 PM clearance cycle.",
            createdAt = now - 4 * day,
            isReviewed = true
        ),
        Feedback(
            feedbackId = "FB-505",
            studentId = "anon_02",
            studentName = "Anonymous",
            category = FeedbackCategory.GENERAL_SUGGESTIONS,
            description = "Can we have a book swap corner or small mini-library rack near the hostel lobby sofa area?",
            rating = 4,
            isAnonymous = true,
            adminResponse = "",
            createdAt = now - 5 * day,
            isReviewed = false
        ),
        Feedback(
            feedbackId = "FB-506",
            studentId = studentUser4.uid,
            studentName = studentUser4.fullName,
            category = FeedbackCategory.HOSTEL_FACILITIES,
            description = "The gym treadmill belt slips when running above 8 km/h. Maintenance is urgently needed before someone slips.",
            rating = 2,
            isAnonymous = false,
            adminResponse = "Gym technician has been scheduled for inspection.",
            createdAt = now - 6 * day,
            isReviewed = true
        ),
        Feedback(
            feedbackId = "FB-507",
            studentId = studentUser5.uid,
            studentName = studentUser5.fullName,
            category = FeedbackCategory.MESS_FOOD,
            description = "South Indian options like dosa night are very good. Please maintain this quality.",
            rating = 5,
            isAnonymous = false,
            adminResponse = "",
            createdAt = now - 7 * day,
            isReviewed = false
        ),
        Feedback(
            feedbackId = "FB-508",
            studentId = "anon_03",
            studentName = "Anonymous",
            category = FeedbackCategory.CLEANLINESS,
            description = "Washrooms on 1st floor are cleaned regularly, but liquid hand soap dispensers are often empty by evening.",
            rating = 3,
            isAnonymous = true,
            adminResponse = "",
            createdAt = now - 8 * day,
            isReviewed = false
        ),
        Feedback(
            feedbackId = "FB-509",
            studentId = studentUser6.uid,
            studentName = studentUser6.fullName,
            category = FeedbackCategory.GENERAL_SUGGESTIONS,
            description = "DormDesk app makes it much easier to lodge complaints instead of queueing in the warden office register! Really like the clean interface.",
            rating = 5,
            isAnonymous = false,
            adminResponse = "Thank you Diya! We are glad DormDesk is serving your hostel needs.",
            createdAt = now - 9 * day,
            isReviewed = true
        ),
        Feedback(
            feedbackId = "FB-510",
            studentId = studentUser7.uid,
            studentName = studentUser7.fullName,
            category = FeedbackCategory.STAFF_BEHAVIOUR,
            description = "Electrician Prakash was very courteous and solved our room switch issue quickly.",
            rating = 5,
            isAnonymous = false,
            adminResponse = "Commendation noted. Thank you for your feedback.",
            createdAt = now - 10 * day,
            isReviewed = true
        )
    )

    // Initial Notifications for student Aarav
    fun getInitialNotifications(studentUid: String): List<NotificationItem> = listOf(
        NotificationItem(
            notificationId = "NOTIF-01",
            userId = studentUid,
            title = "Complaint Progress: Resolution Updated",
            message = "Balcony drain cleared by Mohan Lal. Please verify and confirm resolution.",
            isRead = false,
            createdAt = now - 3 * hour,
            type = NotificationType.COMPLAINT,
            referenceId = "DD-1005"
        ),
        NotificationItem(
            notificationId = "NOTIF-02",
            userId = studentUid,
            title = "Maintenance Assigned: #DD-1003",
            message = "Ramesh Kumar (Carpenter) has been assigned to your drawer lock complaint.",
            isRead = false,
            createdAt = now - 10 * hour,
            type = NotificationType.COMPLAINT,
            referenceId = "DD-1003"
        ),
        NotificationItem(
            notificationId = "NOTIF-03",
            userId = studentUid,
            title = "Emergency Notice: Water Supply Cut",
            message = "Rooftop water tank cleaning scheduled Saturday 10 AM to 4 PM.",
            isRead = true,
            createdAt = now - 4 * hour,
            type = NotificationType.ANNOUNCEMENT,
            referenceId = "ANN-201"
        ),
        NotificationItem(
            notificationId = "NOTIF-04",
            userId = studentUid,
            title = "Warden Responded to Feedback",
            message = "Dr. Rajeshwar Sharma responded to your reading hall suggestion.",
            isRead = true,
            createdAt = now - 1 * day,
            type = NotificationType.FEEDBACK,
            referenceId = "FB-501"
        )
    )
}
