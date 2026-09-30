package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ChecklistItem
import com.example.model.Destination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WanderList", appName)
    }

    @Test
    fun `checklistItem serialization works with Android JSONObject`() {
        val original = listOf(
            ChecklistItem(id = "1", title = "Hike Mount Fuji", isCompleted = false),
            ChecklistItem(id = "2", title = "Soak in an onsen", isCompleted = true)
        )
        val json = ChecklistItem.serializeList(original)
        val parsed = ChecklistItem.parseList(json)

        assertEquals(2, parsed.size)
        assertEquals("Hike Mount Fuji", parsed[0].title)
        assertFalse(parsed[0].isCompleted)
        assertEquals("Soak in an onsen", parsed[1].title)
        assertTrue(parsed[1].isCompleted)
    }

    @Test
    fun `destination checklist count is accurate`() {
        val checklist = listOf(
            ChecklistItem(id = "1", title = "Activity 1", isCompleted = true),
            ChecklistItem(id = "2", title = "Activity 2", isCompleted = false),
            ChecklistItem(id = "3", title = "Activity 3", isCompleted = true)
        )
        val destination = Destination(
            title = "Test Trip",
            country = "Test Country",
            activitiesJson = ChecklistItem.serializeList(checklist)
        )

        assertEquals(3, destination.totalChecklistCount)
        assertEquals(2, destination.completedChecklistCount)
    }
}

