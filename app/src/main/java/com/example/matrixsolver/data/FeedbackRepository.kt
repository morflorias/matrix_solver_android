package com.example.matrixsolver.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.feedbackDataStore: DataStore<Preferences> by preferencesDataStore(name = "feedback")

class FeedbackRepository(private val context: Context) {

    companion object {
        private val FEEDBACK_KEY = stringPreferencesKey("feedback_history")
    }

    val feedbackFlow: Flow<List<FeedbackEntry>> = context.feedbackDataStore.data.map { preferences ->
        val jsonString = preferences[FEEDBACK_KEY] ?: "[]"
        parseFeedback(jsonString)
    }

    suspend fun addFeedback(name: String, email: String, message: String) {
        context.feedbackDataStore.edit { preferences ->
            val currentJson = preferences[FEEDBACK_KEY] ?: "[]"
            val entries = parseFeedback(currentJson).toMutableList()
            
            val entry = FeedbackEntry(
                id = System.currentTimeMillis().toString(),
                name = name,
                email = email,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            entries.add(0, entry)
            
            val jsonArray = JSONArray()
            entries.forEach { jsonArray.put(entryToJson(it)) }
            preferences[FEEDBACK_KEY] = jsonArray.toString()
        }
    }

    suspend fun clearFeedback() {
        context.feedbackDataStore.edit { preferences ->
            preferences[FEEDBACK_KEY] = "[]"
        }
    }

    private fun parseFeedback(jsonString: String): List<FeedbackEntry> {
        val entries = mutableListOf<FeedbackEntry>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                entries.add(FeedbackEntry(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    email = obj.getString("email"),
                    message = obj.getString("message"),
                    timestamp = obj.getLong("timestamp")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return entries
    }

    private fun entryToJson(entry: FeedbackEntry): JSONObject {
        return JSONObject().apply {
            put("id", entry.id)
            put("name", entry.name)
            put("email", entry.email)
            put("message", entry.message)
            put("timestamp", entry.timestamp)
        }
    }
}

data class FeedbackEntry(
    val id: String,
    val name: String,
    val email: String,
    val message: String,
    val timestamp: Long
)
