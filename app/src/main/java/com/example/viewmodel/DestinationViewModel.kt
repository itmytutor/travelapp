package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DestinationRepository
import com.example.model.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    PRIORITY("Priority"),
    TARGET_DATE("Target Date"),
    TITLE("A - Z"),
    BUDGET("Budget")
}

data class BucketStats(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val upcomingCount: Int = 0,
    val cancelledCount: Int = 0,
    val visitedCount: Int = 0,
    val planningCount: Int = 0,
    val dreamingCount: Int = 0,
    val bookedCount: Int = 0,
    val visitedCountriesCount: Int = 0,
    val totalCountriesCount: Int = 0,
    val totalEstimatedBudget: Double = 0.0,
    val completionPercentage: Float = 0f
)

sealed class DestinationEvent {
    data class DestinationVisited(val destination: Destination) : DestinationEvent()
    data class ShowMessage(val message: String) : DestinationEvent()
}

class DestinationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DestinationRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DestinationRepository(db.destinationDao())
    }

    val allDestinations: StateFlow<List<Destination>> = repository.allDestinations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val visitedDestinations: StateFlow<List<Destination>> = repository.visitedDestinations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _filterStatus = MutableStateFlow("All")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    private val _filterCategory = MutableStateFlow("All")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.PRIORITY)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow: SharedFlow<DestinationEvent> = _eventFlow.asSharedFlow()

    // Filtered and sorted destinations
    val filteredDestinations: StateFlow<List<Destination>> = combine(
        allDestinations,
        _filterStatus,
        _filterCategory,
        _searchQuery,
        _sortBy
    ) { list, status, category, query, sort ->
        var result = list

        if (status != "All") {
            result = when (status) {
                "Completed" -> result.filter { it.isCompleted }
                "Upcoming" -> result.filter { it.isUpcoming }
                "Cancelled" -> result.filter { it.isCancelled }
                else -> result.filter { it.status.equals(status, ignoreCase = true) }
            }
        }

        if (category != "All") {
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.title.lowercase().contains(q) ||
                it.country.lowercase().contains(q) ||
                it.cityOrRegion.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.notes.lowercase().contains(q)
            }
        }

        when (sort) {
            SortOption.PRIORITY -> {
                result.sortedWith(
                    compareByDescending<Destination> { it.priority == "High" }
                        .thenByDescending { it.priority == "Medium" }
                        .thenByDescending { it.createdAt }
                )
            }
            SortOption.TARGET_DATE -> {
                result.sortedWith(
                    compareBy<Destination> { it.targetDate.isEmpty() }
                        .thenBy { it.targetDate }
                )
            }
            SortOption.TITLE -> {
                result.sortedBy { it.title.lowercase() }
            }
            SortOption.BUDGET -> {
                result.sortedByDescending { it.estimatedBudget }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Comprehensive Bucket Statistics
    val stats: StateFlow<BucketStats> = allDestinations.combine(visitedDestinations) { all, visited ->
        val total = all.size
        val completedSize = all.count { it.isCompleted }
        val upcomingSize = all.count { it.isUpcoming }
        val cancelledSize = all.count { it.isCancelled }
        val visitedSize = all.count { it.status.equals("Visited", ignoreCase = true) }
        val planningSize = all.count { it.status.equals("Planning", ignoreCase = true) }
        val dreamingSize = all.count { it.status.equals("Dreaming", ignoreCase = true) }
        val bookedSize = all.count { it.status.equals("Booked", ignoreCase = true) }
        val visitedCountries = all.filter { it.isCompleted }
            .map { it.country.trim().lowercase() }.filter { it.isNotBlank() }.distinct().size
        val totalCountries = all.map { it.country.trim().lowercase() }.filter { it.isNotBlank() }.distinct().size
        val totalBudget = all.sumOf { it.estimatedBudget }
        val completion = if (total > 0) (completedSize.toFloat() / total.toFloat()) else 0f

        BucketStats(
            totalCount = total,
            completedCount = completedSize,
            upcomingCount = upcomingSize,
            cancelledCount = cancelledSize,
            visitedCount = visitedSize,
            planningCount = planningSize,
            dreamingCount = dreamingSize,
            bookedCount = bookedSize,
            visitedCountriesCount = visitedCountries,
            totalCountriesCount = totalCountries,
            totalEstimatedBudget = totalBudget,
            completionPercentage = completion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BucketStats()
    )

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortBy(sort: SortOption) {
        _sortBy.value = sort
    }

    fun saveDestination(destination: Destination) {
        viewModelScope.launch {
            if (destination.id == 0L) {
                repository.insert(destination)
                _eventFlow.emit(DestinationEvent.ShowMessage("Added \"${destination.title}\" to bucket list!"))
            } else {
                repository.update(destination)
                _eventFlow.emit(DestinationEvent.ShowMessage("Updated \"${destination.title}\""))
            }
        }
    }

    fun deleteDestination(destination: Destination) {
        viewModelScope.launch {
            repository.delete(destination)
            _eventFlow.emit(DestinationEvent.ShowMessage("Removed \"${destination.title}\" from bucket list"))
        }
    }

    fun toggleVisited(destination: Destination) {
        viewModelScope.launch {
            val wasVisited = destination.status == "Visited"
            repository.toggleVisited(destination)
            if (!wasVisited) {
                _eventFlow.emit(DestinationEvent.DestinationVisited(destination))
            } else {
                _eventFlow.emit(DestinationEvent.ShowMessage("Destination moved back to Planning"))
            }
        }
    }

    fun toggleActivity(destination: Destination, activityId: String) {
        viewModelScope.launch {
            repository.toggleActivity(destination, activityId)
        }
    }

    fun addActivity(destination: Destination, activityTitle: String) {
        viewModelScope.launch {
            repository.addActivity(destination, activityTitle)
        }
    }

    fun deleteActivity(destination: Destination, activityId: String) {
        viewModelScope.launch {
            repository.deleteActivity(destination, activityId)
        }
    }

    fun updateRating(destination: Destination, rating: Int) {
        viewModelScope.launch {
            repository.update(destination.copy(rating = rating))
        }
    }

    fun restoreSamples() {
        viewModelScope.launch {
            repository.restoreSeedData()
            _eventFlow.emit(DestinationEvent.ShowMessage("Restored dream destinations!"))
        }
    }
}
