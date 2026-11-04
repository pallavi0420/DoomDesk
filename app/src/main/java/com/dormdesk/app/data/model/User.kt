package com.dormdesk.app.data.model

enum class UserRole {
    STUDENT,
    WARDEN;

    val displayName: String
        get() = when (this) {
            STUDENT -> "Resident Student"
            WARDEN -> "Hostel Warden / Admin"
        }
}

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val studentId: String = "",
    val phone: String = "",
    val hostel: String = "Tagore Hostel",
    val block: String = "Block B",
    val roomNumber: String = "204",
    val role: UserRole = UserRole.STUDENT
)

object DemoAccounts {
    val SampleStudent = User(
        uid = "stu_aarav_sharma",
        fullName = "Aarav Sharma",
        email = "aarav.student@dormdesk.edu",
        studentId = "STU-2023-4412",
        phone = "+91 98765 43210",
        hostel = "Tagore Hostel",
        block = "Block B",
        roomNumber = "204",
        role = UserRole.STUDENT
    )

    val SampleWarden = User(
        uid = "warden_dr_mehta",
        fullName = "Dr. Rajesh Mehta",
        email = "warden.mehta@dormdesk.edu",
        studentId = "EMP-WAR-008",
        phone = "+91 94123 78901",
        hostel = "Tagore Hostel",
        block = "Administration",
        roomNumber = "Office 01",
        role = UserRole.WARDEN
    )

    val AllDemoStudents = listOf(
        SampleStudent,
        User(
            uid = "stu_diya_patel",
            fullName = "Diya Patel",
            email = "diya.p@dormdesk.edu",
            studentId = "STU-2023-4413",
            phone = "+91 98765 43211",
            hostel = "Tagore Hostel",
            block = "Block A",
            roomNumber = "102",
            role = UserRole.STUDENT
        ),
        User(
            uid = "stu_rohan_verma",
            fullName = "Rohan Verma",
            email = "rohan.v@dormdesk.edu",
            studentId = "STU-2023-4414",
            phone = "+91 98765 43212",
            hostel = "Tagore Hostel",
            block = "Block B",
            roomNumber = "315",
            role = UserRole.STUDENT
        ),
        User(
            uid = "stu_ananya_singh",
            fullName = "Ananya Singh",
            email = "ananya.s@dormdesk.edu",
            studentId = "STU-2023-4415",
            phone = "+91 98765 43213",
            hostel = "Tagore Hostel",
            block = "Block C",
            roomNumber = "108",
            role = UserRole.STUDENT
        ),
        User(
            uid = "stu_karan_jain",
            fullName = "Karan Jain",
            email = "karan.j@dormdesk.edu",
            studentId = "STU-2023-4416",
            phone = "+91 98765 43214",
            hostel = "Tagore Hostel",
            block = "Block A",
            roomNumber = "210",
            role = UserRole.STUDENT
        ),
        User(
            uid = "stu_sneha_nair",
            fullName = "Sneha Nair",
            email = "sneha.n@dormdesk.edu",
            studentId = "STU-2023-4417",
            phone = "+91 98765 43215",
            hostel = "Tagore Hostel",
            block = "Block B",
            roomNumber = "112",
            role = UserRole.STUDENT
        ),
        User(
            uid = "stu_vikram_reddy",
            fullName = "Vikram Reddy",
            email = "vikram.r@dormdesk.edu",
            studentId = "STU-2023-4418",
            phone = "+91 98765 43216",
            hostel = "Tagore Hostel",
            block = "Block C",
            roomNumber = "305",
            role = UserRole.STUDENT
        ),
        User(
            uid = "stu_priya_sharma",
            fullName = "Priya Sharma",
            email = "priya.s@dormdesk.edu",
            studentId = "STU-2023-4419",
            phone = "+91 98765 43217",
            hostel = "Tagore Hostel",
            block = "Block B",
            roomNumber = "206",
            role = UserRole.STUDENT
        )
    )

    val AllDemoWardens = listOf(
        SampleWarden,
        User(
            uid = "warden_mrs_sharma",
            fullName = "Mrs. Sunita Sharma",
            email = "warden.sunita@dormdesk.edu",
            studentId = "EMP-WAR-009",
            phone = "+91 94123 78902",
            hostel = "Gargi Hostel",
            block = "Administration",
            roomNumber = "Office 02",
            role = UserRole.WARDEN
        )
    )
}
