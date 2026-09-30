package com.example.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("title", title)
        json.put("isCompleted", isCompleted)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): ChecklistItem {
            return ChecklistItem(
                id = json.optString("id", UUID.randomUUID().toString()),
                title = json.optString("title", ""),
                isCompleted = json.optBoolean("isCompleted", false)
            )
        }

        fun parseList(jsonString: String): List<ChecklistItem> {
            if (jsonString.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<ChecklistItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(fromJson(obj))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun serializeList(items: List<ChecklistItem>): String {
            val array = JSONArray()
            items.forEach { array.put(it.toJson()) }
            return array.toString()
        }
    }
}
