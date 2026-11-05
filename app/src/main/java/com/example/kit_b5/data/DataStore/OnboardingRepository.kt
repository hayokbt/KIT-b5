package com.example.kit_b5.data.DataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.map

data class Profile(val nickname: String = "", val birthYear: Int? = null, val gender: String? = null)
data class ReminderSettings(
    val enabled: Boolean = true,
    val frequency: String = "毎週",
    val day: Int = 0,
    val monthDay: Int = 1,
    val hour: Int = 19,
    val minute: Int = 0
)

data class UserSettings(val profile: Profile, val reminder: ReminderSettings?)

class OnboardingRepository(private val store: DataStore<Preferences>) {
    private object Keys {
        val completed = booleanPreferencesKey("completed")
        val nickname = stringPreferencesKey("nickname")
        val year = intPreferencesKey("birth_year")
        val gender = stringPreferencesKey("gender")
        val enabled = booleanPreferencesKey("reminder_enabled")
        val frequency = stringPreferencesKey("reminder_frequency")
        val day = intPreferencesKey("reminder_weekday")
        val monthDay = intPreferencesKey("reminder_month_day")
        val hour = intPreferencesKey("reminder_hour")
        val minute = intPreferencesKey("reminder_minute")
    }
    val completed = store.data.map { it[Keys.completed] ?: false }
    val settings = store.data.map {
        UserSettings(
            profile = Profile(it[Keys.nickname] ?: "", it[Keys.year], it[Keys.gender]),
            reminder = if (it[Keys.enabled] == null) null else ReminderSettings(
                enabled = it[Keys.enabled] ?: false,
                frequency = it[Keys.frequency] ?: "毎週",
                day = it[Keys.day] ?: 0,
                monthDay = it[Keys.monthDay] ?: 1,
                hour = it[Keys.hour] ?: 19,
                minute = it[Keys.minute] ?: 0
            )
        )
    }
    val profile = settings.map { it.profile }
    val reminder = settings.map { it.reminder }

    suspend fun saveSettings(profile: Profile, reminder: ReminderSettings) {
        require(profile.nickname.isNotBlank())
        store.edit {
            it[Keys.nickname] = profile.nickname.trim()
            profile.birthYear?.let { year -> it[Keys.year] = year } ?: it.remove(Keys.year)
            profile.gender?.let { gender -> it[Keys.gender] = gender } ?: it.remove(Keys.gender)
            it[Keys.enabled] = reminder.enabled
            it[Keys.frequency] = reminder.frequency
            it[Keys.day] = reminder.day
            it[Keys.monthDay] = reminder.monthDay
            it[Keys.hour] = reminder.hour
            it[Keys.minute] = reminder.minute
        }
    }

    suspend fun saveProfile(profile: Profile) {
        store.edit {
            it[Keys.nickname] = profile.nickname.trim()
            profile.birthYear?.let { year -> it[Keys.year] = year } ?: it.remove(Keys.year)
            profile.gender?.let { gender -> it[Keys.gender] = gender } ?: it.remove(Keys.gender)
        }
    }

    suspend fun complete(reminder: ReminderSettings?) {
        store.edit {
            reminder?.let { setting ->
                it[Keys.enabled] = setting.enabled
                it[Keys.frequency] = setting.frequency
                it[Keys.day] = setting.day
                it[Keys.monthDay] = setting.monthDay
                it[Keys.hour] = setting.hour
                it[Keys.minute] = setting.minute
            }
            it[Keys.completed] = true
        }
    }
}
