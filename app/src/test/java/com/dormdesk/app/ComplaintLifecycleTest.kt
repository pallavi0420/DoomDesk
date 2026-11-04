package com.dormdesk.app

import com.dormdesk.app.data.model.ComplaintCategory
import com.dormdesk.app.data.model.ComplaintPriority
import com.dormdesk.app.data.model.ComplaintStatus
import com.dormdesk.app.data.repository.InMemoryDormDeskRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ComplaintLifecycleTest {

    private lateinit var repository: InMemoryDormDeskRepository

    @Before
    fun setup() {
        repository = InMemoryDormDeskRepository()
    }

    @Test
    fun testComplaintCreationAndTimeline() = runBlocking {
        val result = repository.createComplaint(
            title = "AC Remote not working",
            category = ComplaintCategory.ELECTRICAL,
            description = "Buttons do not register",
            priority = ComplaintPriority.MEDIUM,
            location = "Room 204",
            imageUrl = null
        )

        assertTrue(result.isSuccess)
        val created = result.getOrNull()
        assertNotNull(created)
        assertEquals(ComplaintStatus.SUBMITTED, created!!.status)
        assertEquals(1, created.activityLogs.size)

        // Warden assigns personnel & moves to IN_PROGRESS
        val updateResult = repository.updateComplaintStatus(
            complaintId = created.complaintId,
            newStatus = ComplaintStatus.IN_PROGRESS,
            assignedStaffName = "Ramesh Kumar",
            remarks = "Replacement ordered",
            resolutionNotes = null,
            completionPhotoUrl = null
        )

        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()!!
        assertEquals(ComplaintStatus.IN_PROGRESS, updated.status)
        assertEquals("Ramesh Kumar", updated.assignedStaffName)
        assertEquals(2, updated.activityLogs.size)

        // Warden marks RESOLVED
        val resolvedResult = repository.updateComplaintStatus(
            complaintId = created.complaintId,
            newStatus = ComplaintStatus.RESOLVED,
            assignedStaffName = null,
            remarks = null,
            resolutionNotes = "Remote replaced and batteries tested",
            completionPhotoUrl = null
        )

        assertTrue(resolvedResult.isSuccess)
        val resolved = resolvedResult.getOrNull()!!
        assertEquals(ComplaintStatus.RESOLVED, resolved.status)
        assertFalse(resolved.isConfirmedByStudent)

        // Student confirms and rates
        val confirmedResult = repository.confirmComplaintResolution(
            complaintId = created.complaintId,
            rating = 5,
            feedback = "Super fast repair!"
        )

        assertTrue(confirmedResult.isSuccess)
        val closed = confirmedResult.getOrNull()!!
        assertEquals(ComplaintStatus.CLOSED, closed.status)
        assertTrue(closed.isConfirmedByStudent)
        assertEquals(5, closed.studentRating)
        assertEquals("Super fast repair!", closed.studentFeedback)
    }

    @Test
    fun testReopenComplaint() = runBlocking {
        val cmpId = "CMP-2026-1003" // Resolved in demo dataset
        val result = repository.reopenComplaint(cmpId, "Lock still jams when closing drawer")

        assertTrue(result.isSuccess)
        val reopened = result.getOrNull()!!
        assertEquals(ComplaintStatus.RECEIVED, reopened.status)
        assertEquals("Lock still jams when closing drawer", reopened.reopenReason)
    }
}
