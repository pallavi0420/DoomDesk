package com.dormdesk.app

import com.dormdesk.app.data.model.*
import com.dormdesk.app.data.repository.AuthRepository
import com.dormdesk.app.data.repository.ComplaintRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AuthAndComplaintTest {

    @Test
    fun testWardenRegistersTheirOwnAccountAndLogsInWithCredentials() = runBlocking {
        val authRepo = AuthRepository()

        // 1. Warden creates their own account with custom credentials
        val customWarden = User(
            fullName = "Prof. Rakesh Mehta",
            studentId = "STAFF-W101",
            email = "rakesh.mehta@myhostel.edu",
            phone = "+91 98765 00001",
            hostel = "Tagore Hostel",
            block = "Admin Block",
            roomNumber = "A-101",
            role = UserRole.WARDEN
        )
        val regResult = authRepo.register(customWarden, "WardenPass@2026")
        assertTrue("Warden registration should succeed", regResult.isSuccess)

        val registeredUser = regResult.getOrNull()
        assertNotNull(registeredUser)
        assertEquals("Prof. Rakesh Mehta", registeredUser?.fullName)
        assertEquals(UserRole.WARDEN, registeredUser?.role)
        assertEquals("rakesh.mehta@myhostel.edu", registeredUser?.email)

        // Logout
        authRepo.logout()
        assertNull(authRepo.getCurrentUserSync())

        // 2. Attempt login with wrong password -> must be rejected
        val wrongPassResult = authRepo.login("rakesh.mehta@myhostel.edu", "wrongPassword", expectedRole = UserRole.WARDEN)
        assertTrue("Login with wrong password must fail", wrongPassResult.isFailure)
        assertTrue(wrongPassResult.exceptionOrNull()?.message?.contains("Incorrect password") == true)

        // 3. Attempt login into Resident portal -> must be rejected
        val wrongRoleResult = authRepo.login("rakesh.mehta@myhostel.edu", "WardenPass@2026", expectedRole = UserRole.STUDENT)
        assertTrue("Warden logging into student portal must fail", wrongRoleResult.isFailure)
        assertTrue(wrongRoleResult.exceptionOrNull()?.message?.contains("Warden") == true)

        // 4. Correct login on Warden portal -> must succeed
        val loginResult = authRepo.login("rakesh.mehta@myhostel.edu", "WardenPass@2026", expectedRole = UserRole.WARDEN)
        assertTrue("Login with valid credentials must succeed", loginResult.isSuccess)
        assertEquals("Prof. Rakesh Mehta", loginResult.getOrNull()?.fullName)
        assertEquals(UserRole.WARDEN, loginResult.getOrNull()?.role)
    }

    @Test
    fun testStudentRegistersTheirOwnAccountAndLogsIn() = runBlocking {
        val authRepo = AuthRepository()

        // Student creates own account
        val customStudent = User(
            fullName = "Ananya Roy",
            studentId = "2026CS501",
            email = "ananya.roy@mycollege.edu",
            phone = "+91 91234 99999",
            hostel = "Tagore Hostel",
            block = "Block C",
            roomNumber = "302",
            role = UserRole.STUDENT
        )
        val regResult = authRepo.register(customStudent, "StudentPass@2026")
        assertTrue(regResult.isSuccess)

        authRepo.logout()

        // Student trying to log into Warden portal -> rejected
        val wardenPortalAttempt = authRepo.login("ananya.roy@mycollege.edu", "StudentPass@2026", expectedRole = UserRole.WARDEN)
        assertTrue(wardenPortalAttempt.isFailure)
        assertTrue(wardenPortalAttempt.exceptionOrNull()?.message?.contains("Student") == true)

        // Student logging into Resident portal -> succeeds
        val residentPortalAttempt = authRepo.login("ananya.roy@mycollege.edu", "StudentPass@2026", expectedRole = UserRole.STUDENT)
        assertTrue(residentPortalAttempt.isSuccess)
        assertEquals(UserRole.STUDENT, residentPortalAttempt.getOrNull()?.role)
    }

    @Test
    fun testDeleteWardenAccountPermanently() = runBlocking {
        val authRepo = AuthRepository()
        val warden = User(
            fullName = "Warden To Delete",
            studentId = "STAFF-DEL",
            email = "delete.me@hostel.edu",
            role = UserRole.WARDEN
        )
        val reg = authRepo.register(warden, "tempPass123").getOrThrow()
        assertEquals(reg, authRepo.getCurrentUserSync())

        // Delete account
        val deleteResult = authRepo.deleteAccount(reg.uid)
        assertTrue(deleteResult.isSuccess)
        assertNull(authRepo.getCurrentUserSync())

        // Verify account is removed and cannot log in anymore
        val retryLogin = authRepo.login("delete.me@hostel.edu", "tempPass123", expectedRole = UserRole.WARDEN)
        assertTrue(retryLogin.isFailure)
    }

    @Test
    fun testWardenUpdateComplaintStatusAndSubmitRemarks() = runBlocking {
        val complaintRepo = ComplaintRepository()
        val complaints = complaintRepo.complaints.value
        assertTrue(complaints.isNotEmpty())
        val target = complaints.first()

        // 1. Update status
        val updateResult = complaintRepo.updateStatus(
            complaintId = target.complaintId,
            newStatus = ComplaintStatus.RECEIVED,
            remarks = "Complaint acknowledged by warden"
        )
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrThrow()
        assertEquals(ComplaintStatus.RECEIVED, updated.status)

        // 2. Submit remarks/feedback on complaint
        val feedbackResult = complaintRepo.addRemarks(
            complaintId = target.complaintId,
            remarks = "Plumber scheduled for 3:00 PM today with replacement valves."
        )
        assertTrue(feedbackResult.isSuccess)
        val withFeedback = feedbackResult.getOrThrow()
        assertEquals("Plumber scheduled for 3:00 PM today with replacement valves.", withFeedback.remarks)
    }
}
