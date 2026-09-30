package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Destination
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {
    @Query("SELECT * FROM destinations ORDER BY priority = 'High' DESC, priority = 'Medium' DESC, createdAt DESC")
    fun getAllDestinations(): Flow<List<Destination>>

    @Query("SELECT * FROM destinations WHERE id = :id LIMIT 1")
    fun getDestinationById(id: Long): Flow<Destination?>

    @Query("SELECT * FROM destinations WHERE status = 'Visited' ORDER BY visitedDate DESC, createdAt DESC")
    fun getVisitedDestinations(): Flow<List<Destination>>

    @Query("SELECT COUNT(*) FROM destinations")
    fun getDestinationCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM destinations WHERE status = 'Visited'")
    fun getVisitedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(destination: Destination): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(destinations: List<Destination>)

    @Update
    suspend fun update(destination: Destination)

    @Delete
    suspend fun delete(destination: Destination)

    @Query("DELETE FROM destinations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM destinations")
    suspend fun clearAll()
}
