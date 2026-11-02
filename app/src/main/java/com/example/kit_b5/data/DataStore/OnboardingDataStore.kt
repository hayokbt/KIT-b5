package com.example.kit_b5.data.DataStore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.onboardingDataStore by preferencesDataStore(name = "onboarding")
