package com.dormdesk.app

import com.dormdesk.app.utils.DateUtils
import com.dormdesk.app.utils.IdGenerator
import org.junit.Assert.*
import org.junit.Test

class IdGeneratorAndDateTest {

    @Test
    fun testComplaintIdFormat() {
        val id = IdGenerator.generateComplaintId()
        assertTrue(id.startsWith("CMP-2026-"))
        assertEquals(13, id.length)
    }

    @Test
    fun testFeedbackIdFormat() {
        val id = IdGenerator.generateFeedbackId()
        assertTrue(id.startsWith("FB-2026-"))
    }

    @Test
    fun testAnnouncementIdFormat() {
        val id = IdGenerator.generateAnnouncementId()
        assertTrue(id.startsWith("ANN-2026-"))
    }

    @Test
    fun testDateFormatting() {
        val now = System.currentTimeMillis()
        val formatted = DateUtils.formatDateOnly(now)
        assertTrue(formatted.isNotBlank())
        assertEquals("Just now", DateUtils.getRelativeTimeSpan(now))
    }
}
