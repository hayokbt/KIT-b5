package com.example.kit_b5.data.DataStore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import org.json.JSONArray

private val Context.wishStore by preferencesDataStore("wishes")
data class Wish(
    val values: List<String> = emptyList(),
    val place: String = "",
    val people: Set<String> = emptySet(),
    val treatment: String = "",
    val procedures: Map<String, String> = emptyMap(),
    val shared: Map<String, Long> = emptyMap(),
    val donation: String = ""
)
class WishRepository(private val store: androidx.datastore.core.DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.wishStore)
    private val key = stringPreferencesKey("wish")
    val wish = store.data.map { preferences ->
        val json = JSONObject(preferences[key] ?: "{}")
        val values = json.optJSONArray("values") ?: JSONArray()
        val people = json.optJSONArray("people") ?: JSONArray()
        val procedures = json.optJSONObject("procedures") ?: JSONObject()
        val shared = json.optJSONObject("shared") ?: JSONObject()
        Wish(
            (0 until values.length()).map { values.getString(it) },
            json.optString("place"),
            (0 until people.length()).map { people.getString(it) }.toSet(),
            json.optString("treatment"),
            procedures.keys().asSequence().associateWith { procedures.getString(it) },
            shared.keys().asSequence().associateWith { shared.getLong(it) },
            json.optString("donation")
        )
    }
    suspend fun save(wish: Wish) {
        val json = JSONObject().apply {
            put("values", JSONArray(wish.values))
            put("place", wish.place)
            put("people", JSONArray(wish.people.toList()))
            put("treatment", wish.treatment)
            put("procedures", JSONObject(wish.procedures))
            put("shared", JSONObject(wish.shared))
            put("donation", wish.donation)
        }
        store.edit { it[key] = json.toString() }
    }
}
