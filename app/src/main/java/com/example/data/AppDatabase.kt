package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.ChecklistItem
import com.example.model.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Destination::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun destinationDao(): DestinationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wanderlist_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialDestinations(database.destinationDao())
                    }
                }
            }
        }

        suspend fun populateInitialDestinations(dao: DestinationDao) {
            val samples = listOf(
                Destination(
                    title = "Kyoto Bamboo Groves & Ancient Temples",
                    country = "Japan",
                    cityOrRegion = "Kyoto & Arashiyama",
                    category = "Culture",
                    priority = "High",
                    status = "Upcoming",
                    targetDate = "Autumn 2026",
                    estimatedBudget = 2800.0,
                    currency = "USD",
                    notes = "Walk Arashiyama Bamboo Grove at dawn before crowds. Experience a traditional tea ceremony in historic Gion district.",
                    activitiesJson = ChecklistItem.serializeList(
                        listOf(
                            ChecklistItem(title = "Walk through serene Arashiyama bamboo path", isCompleted = false),
                            ChecklistItem(title = "Climb Fushimi Inari shrine 1,000 torii gates", isCompleted = true),
                            ChecklistItem(title = "Visit golden Kinkaku-ji temple reflection pond", isCompleted = false),
                            ChecklistItem(title = "Attend traditional matcha ceremony in Gion", isCompleted = false)
                        )
                    ),
                    rating = 0,
                    imagePreset = "culture"
                ),
                Destination(
                    title = "Santorini Sunset & Caldera Cruise",
                    country = "Greece",
                    cityOrRegion = "Oia & Fira",
                    category = "Beach",
                    priority = "High",
                    status = "Dreaming",
                    targetDate = "Summer 2027",
                    estimatedBudget = 3200.0,
                    currency = "EUR",
                    notes = "Stay in a whitewashed cave house overlooking the azure Aegean sea. Hike the cliffside trail between Fira and Oia.",
                    activitiesJson = ChecklistItem.serializeList(
                        listOf(
                            ChecklistItem(title = "Hike scenic 10km caldera trail from Fira to Oia", isCompleted = false),
                            ChecklistItem(title = "Catamaran sunset sailing around volcanic islands", isCompleted = false),
                            ChecklistItem(title = "Swim at unique Red Beach and volcanic hot springs", isCompleted = false),
                            ChecklistItem(title = "Wine tasting local Assyrtiko at Santo Wines cliff", isCompleted = false)
                        )
                    ),
                    rating = 0,
                    imagePreset = "beach"
                ),
                Destination(
                    title = "Banff & Lake Louise Glacial Peaks",
                    country = "Canada",
                    cityOrRegion = "Alberta Rockies",
                    category = "Mountain",
                    priority = "High",
                    status = "Completed",
                    targetDate = "September 2025",
                    visitedDate = 1726000000000L,
                    rating = 5,
                    estimatedBudget = 1900.0,
                    currency = "CAD",
                    notes = "Rented a red canoe on Lake Louise at 7 AM. The water was breathtaking vivid turquoise, framed by Mount Victoria glacier!",
                    activitiesJson = ChecklistItem.serializeList(
                        listOf(
                            ChecklistItem(title = "Canoe across turquoise Lake Louise", isCompleted = true),
                            ChecklistItem(title = "Hike Plain of Six Glaciers historic tea house trail", isCompleted = true),
                            ChecklistItem(title = "Ride Banff Gondola up Sulphur Mountain ridge", isCompleted = true),
                            ChecklistItem(title = "Drive the iconic Icefields Parkway to Columbia Glacier", isCompleted = true)
                        )
                    ),
                    imagePreset = "mountain"
                ),
                Destination(
                    title = "Machu Picchu & Sacred Valley of the Incas",
                    country = "Peru",
                    cityOrRegion = "Cusco Region",
                    category = "Wonder",
                    priority = "Medium",
                    status = "Upcoming",
                    targetDate = "May 2027",
                    estimatedBudget = 2400.0,
                    currency = "USD",
                    notes = "Acclimatize in Cusco cobblestone streets first. Take the glass-roof Vistadome train winding along the Urubamba river.",
                    activitiesJson = ChecklistItem.serializeList(
                        listOf(
                            ChecklistItem(title = "Enter Sun Gate at sunrise over ancient citadel", isCompleted = false),
                            ChecklistItem(title = "Explore Ollantaytambo terraces in the Sacred Valley", isCompleted = false),
                            ChecklistItem(title = "Taste traditional Peruvian ceviche & lomo saltado", isCompleted = false),
                            ChecklistItem(title = "Hike Huayna Picchu mountain overlooking ruins", isCompleted = false)
                        )
                    ),
                    rating = 0,
                    imagePreset = "culture"
                ),
                Destination(
                    title = "Serengeti Great Migration & Crater",
                    country = "Tanzania",
                    cityOrRegion = "Serengeti & Ngorongoro",
                    category = "Safari",
                    priority = "Medium",
                    status = "Cancelled",
                    targetDate = "Winter 2028",
                    estimatedBudget = 4500.0,
                    currency = "USD",
                    notes = "Witness millions of wildebeest crossing the savannah. Camp under the boundless African night sky. (Postponed safari).",
                    activitiesJson = ChecklistItem.serializeList(
                        listOf(
                            ChecklistItem(title = "Hot air balloon flight over sunrise savannah", isCompleted = false),
                            ChecklistItem(title = "Spot the Big 5 on a full-day game drive", isCompleted = false),
                            ChecklistItem(title = "Descend 600m into Ngorongoro volcanic crater", isCompleted = false),
                            ChecklistItem(title = "Learn traditional beadwork at a Maasai boma", isCompleted = false)
                        )
                    ),
                    rating = 0,
                    imagePreset = "safari"
                )
            )
            dao.insertAll(samples)
        }
    }
}
