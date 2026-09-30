package com.example

import com.example.model.ChecklistItem
import com.example.model.Destination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun destination_checklistProgressCalculatesCorrectly() {
        val items = listOf(
            ChecklistItem(id = "1", title = "Book flight", isCompleted = true),
            ChecklistItem(id = "2", title = "Reserve hotel", isCompleted = false)
        )
        val destination = Destination(
            title = "Tokyo",
            activitiesJson = ChecklistItem.serializeList(items)
        )

        assertEquals(2, destination.totalChecklistCount)
        assertEquals(1, destination.completedChecklistCount)
        assertTrue(destination.checklist.isNotEmpty())
    }
}


