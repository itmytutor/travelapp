package com.example.data

import com.example.model.ChecklistItem
import com.example.model.Destination
import kotlinx.coroutines.flow.Flow

class DestinationRepository(private val dao: DestinationDao) {
    val allDestinations: Flow<List<Destination>> = dao.getAllDestinations()
    val visitedDestinations: Flow<List<Destination>> = dao.getVisitedDestinations()
    val totalCount: Flow<Int> = dao.getDestinationCount()
    val visitedCount: Flow<Int> = dao.getVisitedCount()

    fun getDestinationById(id: Long): Flow<Destination?> = dao.getDestinationById(id)

    suspend fun insert(destination: Destination): Long = dao.insert(destination)

    suspend fun update(destination: Destination) = dao.update(destination)

    suspend fun delete(destination: Destination) = dao.delete(destination)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun toggleVisited(destination: Destination, rating: Int = 5) {
        val isNowVisited = destination.status != "Visited"
        val updated = destination.copy(
            status = if (isNowVisited) "Visited" else "Planning",
            visitedDate = if (isNowVisited) System.currentTimeMillis() else null,
            rating = if (isNowVisited) (if (destination.rating > 0) destination.rating else rating) else 0
        )
        dao.update(updated)
    }

    suspend fun toggleActivity(destination: Destination, activityId: String) {
        val currentItems = destination.checklist
        val updatedItems = currentItems.map {
            if (it.id == activityId) it.copy(isCompleted = !it.isCompleted) else it
        }
        val updated = destination.copy(
            activitiesJson = ChecklistItem.serializeList(updatedItems)
        )
        dao.update(updated)
    }

    suspend fun addActivity(destination: Destination, title: String) {
        if (title.isBlank()) return
        val currentItems = destination.checklist.toMutableList()
        currentItems.add(ChecklistItem(title = title.trim(), isCompleted = false))
        val updated = destination.copy(
            activitiesJson = ChecklistItem.serializeList(currentItems)
        )
        dao.update(updated)
    }

    suspend fun deleteActivity(destination: Destination, activityId: String) {
        val currentItems = destination.checklist.filterNot { it.id == activityId }
        val updated = destination.copy(
            activitiesJson = ChecklistItem.serializeList(currentItems)
        )
        dao.update(updated)
    }

    suspend fun restoreSeedData() {
        AppDatabase.populateInitialDestinations(dao)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
