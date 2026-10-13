package com.example.matrixsolver.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.matrixsolver.domain.model.CalculationRecord
import com.example.matrixsolver.domain.model.OperationType
import com.example.matrixsolver.domain.model.CalculationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "history")

class HistoryRepository(private val context: Context) {

    companion object {
        private val HISTORY_KEY = stringPreferencesKey("calculation_history")
    }

    val historyFlow: Flow<List<CalculationRecord>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[HISTORY_KEY] ?: "[]"
        parseHistory(jsonString)
    }

    suspend fun addRecord(record: CalculationRecord) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[HISTORY_KEY] ?: "[]"
            val records = parseHistory(currentJson).toMutableList()
            records.add(0, record)
            
            val jsonArray = JSONArray()
            records.forEach { jsonArray.put(recordToJson(it)) }
            preferences[HISTORY_KEY] = jsonArray.toString()
        }
    }

    suspend fun deleteRecord(id: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[HISTORY_KEY] ?: "[]"
            val records = parseHistory(currentJson).toMutableList()
            records.removeAll { it.id == id }
            
            val jsonArray = JSONArray()
            records.forEach { jsonArray.put(recordToJson(it)) }
            preferences[HISTORY_KEY] = jsonArray.toString()
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = "[]"
        }
    }

    private fun parseHistory(jsonString: String): List<CalculationRecord> {
        val records = mutableListOf<CalculationRecord>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                try {
                    val obj = jsonArray.getJSONObject(i)
                    records.add(jsonToRecord(obj))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return records
    }

    private fun recordToJson(record: CalculationRecord): JSONObject {
        val obj = JSONObject()
        obj.put("id", record.id)
        obj.put("timestamp", record.timestamp)
        obj.put("operationType", record.operationType.displayName)
        obj.put("matrixSize", record.matrixSize)
        obj.put("matrixA", matrixToJson(record.matrixA))
        if (record.matrixB != null) {
            obj.put("matrixB", matrixToJson(record.matrixB))
        }
        if (record.scalar != null) {
            obj.put("scalar", record.scalar)
        }
        
        val resultObj = JSONObject()
        when (val result = record.result) {
            is CalculationResult.MatrixResult -> {
                resultObj.put("type", "matrix")
                resultObj.put("matrix", matrixToJson(result.matrix))
            }
            is CalculationResult.ScalarResult -> {
                resultObj.put("type", "scalar")
                resultObj.put("value", result.value)
            }
            is CalculationResult.Error -> {
                resultObj.put("type", "error")
                resultObj.put("message", result.message)
            }
        }
        obj.put("result", resultObj)
        
        return obj
    }

    private fun jsonToRecord(json: JSONObject): CalculationRecord {
        val operationTypeStr = json.getString("operationType")
        val operationType = OperationType.fromName(operationTypeStr)
        val matrixSize = json.getInt("matrixSize")
        val matrixA = jsonToMatrix(json.getJSONArray("matrixA"))
        
        val matrixB = if (json.has("matrixB")) {
            jsonToMatrix(json.getJSONArray("matrixB"))
        } else null
        
        val scalar = if (json.has("scalar")) json.getDouble("scalar") else null
        
        val resultObj = json.getJSONObject("result")
        val result = when (resultObj.getString("type")) {
            "matrix" -> CalculationResult.MatrixResult(jsonToMatrix(resultObj.getJSONArray("matrix")))
            "scalar" -> CalculationResult.ScalarResult(resultObj.getDouble("value"))
            else -> CalculationResult.Error(resultObj.optString("message", "Unknown error"))
        }
        
        return CalculationRecord(
            id = json.getString("id"),
            timestamp = json.getLong("timestamp"),
            operationType = operationType,
            matrixSize = matrixSize,
            matrixA = matrixA,
            matrixB = matrixB,
            scalar = scalar,
            result = result
        )
    }

    private fun matrixToJson(matrix: List<List<Double>>): JSONArray {
        val array = JSONArray()
        matrix.forEach { row ->
            val rowArray = JSONArray()
            row.forEach { rowArray.put(it) }
            array.put(rowArray)
        }
        return array
    }

    private fun jsonToMatrix(jsonArray: JSONArray): List<List<Double>> {
        val matrix = mutableListOf<List<Double>>()
        for (i in 0 until jsonArray.length()) {
            val rowArray = jsonArray.getJSONArray(i)
            val row = mutableListOf<Double>()
            for (j in 0 until rowArray.length()) {
                row.add(rowArray.getDouble(j))
            }
            matrix.add(row)
        }
        return matrix
    }
}
