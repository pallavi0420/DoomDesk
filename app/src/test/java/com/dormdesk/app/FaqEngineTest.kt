package com.dormdesk.app

import com.dormdesk.app.data.model.*
import com.dormdesk.app.utils.FaqEngine
import org.junit.Assert.*
import org.junit.Test

class FaqEngineTest {

    private val sampleUser = DemoAccounts.SampleStudent
    private val sampleComplaints = listOf(
        Complaint(
            complaintId = "CMP-2026-1001",
            studentId = sampleUser.uid,
            title = "Fan not spinning",
            category = ComplaintCategory.ELECTRICAL,
            priority = ComplaintPriority.HIGH,
            status = ComplaintStatus.IN_PROGRESS,
            assignedStaffName = "Ramesh Kumar"
        )
    )
    private val sampleAnnouncements = listOf(
        Announcement(
            announcementId = "ANN-101",
            title = "Water Tank Cleaning",
            description = "Tank cleaning this weekend.",
            category = AnnouncementCategory.WATER_SUPPLY,
            priority = AnnouncementPriority.HIGH
        )
    )

    @Test
    fun testPendingComplaintsQuery() {
        val result = FaqEngine.processQuery(
            rawQuery = "What is my complaint status?",
            currentUser = sampleUser,
            userComplaints = sampleComplaints,
            recentAnnouncements = sampleAnnouncements
        )

        assertTrue(result.answer.contains("CMP-2026-1001"))
        assertTrue(result.answer.contains("In Progress") || result.answer.contains("Work in Progress"))
        assertEquals("complaints", result.targetRoute)
    }

    @Test
    fun testRaiseComplaintProcedureQuery() {
        val result = FaqEngine.processQuery(
            rawQuery = "How do I raise a complaint?",
            currentUser = sampleUser,
            userComplaints = sampleComplaints,
            recentAnnouncements = sampleAnnouncements
        )

        assertTrue(result.answer.contains("Complaints"))
        assertTrue(result.answer.contains("Category"))
        assertEquals("raise_complaint", result.targetRoute)
    }

    @Test
    fun testWardenContactQuery() {
        val result = FaqEngine.processQuery(
            rawQuery = "How can I contact the warden?",
            currentUser = sampleUser,
            userComplaints = sampleComplaints,
            recentAnnouncements = sampleAnnouncements
        )

        assertTrue(result.answer.contains("Dr. Rajesh Mehta"))
        assertTrue(result.answer.contains("warden.mehta@dormdesk.edu"))
    }
}
