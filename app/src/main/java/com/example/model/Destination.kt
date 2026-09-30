package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class Destination(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val country: String = "",
    val cityOrRegion: String = "",
    val category: String = "Culture", // Beach, Mountain, Culture, City, Safari, Wonder, Food
    val priority: String = "High", // High, Medium, Someday
    val status: String = "Dreaming", // Dreaming, Planning, Booked, Visited
    val targetDate: String = "", // e.g. "Summer 2026", "October 2026", "2027"
    val estimatedBudget: Double = 0.0,
    val currency: String = "USD", // USD, EUR, GBP, JPY, CAD, AUD
    val notes: String = "",
    val activitiesJson: String = "[]",
    val rating: Int = 0, // 0-5 stars
    val visitedDate: Long? = null,
    val imagePreset: String = "beach", // beach, mountain, culture, city, safari, aurora, island
    val customImageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val checklist: List<ChecklistItem>
        get() = ChecklistItem.parseList(activitiesJson)

    val completedChecklistCount: Int
        get() = checklist.count { it.isCompleted }

    val totalChecklistCount: Int
        get() = checklist.size

    val isCompleted: Boolean
        get() = status.equals("Completed", ignoreCase = true) || status.equals("Visited", ignoreCase = true)

    val isUpcoming: Boolean
        get() = status.equals("Upcoming", ignoreCase = true) || status.equals("Booked", ignoreCase = true) || status.equals("Planning", ignoreCase = true)

    val isCancelled: Boolean
        get() = status.equals("Cancelled", ignoreCase = true)
}

object TravelCategories {
    val ALL = listOf(
        "Beach",
        "Mountain",
        "Culture",
        "City",
        "Safari",
        "Wonder",
        "Food & Wine"
    )
}

object TravelPriorities {
    const val HIGH = "High"
    const val MEDIUM = "Medium"
    const val SOMEDAY = "Someday"

    val ALL = listOf(HIGH, MEDIUM, SOMEDAY)
}

object TravelStatuses {
    const val UPCOMING = "Upcoming"
    const val COMPLETED = "Completed"
    const val CANCELLED = "Cancelled"
    const val DREAMING = "Dreaming"
    const val PLANNING = "Planning"
    const val BOOKED = "Booked"
    const val VISITED = "Visited"

    val ALL = listOf(UPCOMING, COMPLETED, CANCELLED, DREAMING, PLANNING, BOOKED)
    val FILTER_TABS = listOf("All", UPCOMING, COMPLETED, CANCELLED, DREAMING, PLANNING)
}

object PresetStyles {
    val PRESETS = listOf(
        "beach" to "Tropical Beach",
        "mountain" to "Alpine Peak",
        "culture" to "Historic Temple",
        "city" to "Vibrant City",
        "safari" to "Wildlife Safari",
        "aurora" to "Northern Lights",
        "island" to "Island Getaway"
    )
}
