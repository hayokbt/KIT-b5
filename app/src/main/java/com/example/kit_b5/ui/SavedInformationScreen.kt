package com.example.kit_b5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SavedInformationScreen(viewModel: SavedInformationViewModel) {
    val state by viewModel.state.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("保存した情報を読み込めませんでした。")
            }
            else -> Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("保存した情報", style = MaterialTheme.typography.headlineMedium)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("基本情報", style = MaterialTheme.typography.titleLarge)
                        SavedValue("ニックネーム", state.profile.nickname)
                        SavedValue("生まれ年", state.profile.birthYear?.let { "${it}年" } ?: "未設定")
                        SavedValue("性別", state.profile.gender ?: "未設定")
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("リマインド設定", style = MaterialTheme.typography.titleLarge)
                        val reminder = state.reminder
                        if (reminder == null) {
                            Text("リマインド設定は保存されていません。")
                        } else {
                            SavedValue("リマインドを受け取る", if (reminder.enabled) "オン" else "オフ")
                            SavedValue("頻度", reminder.frequency)
                            if (reminder.frequency == "毎週") {
                                SavedValue("曜日", listOf("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日")
                                    .getOrNull(reminder.day) ?: "未設定")
                            }
                            if (reminder.frequency == "毎月") SavedValue("日付", "${reminder.monthDay}日")
                            SavedValue("時刻", String.format(Locale.JAPAN, "%02d:%02d", reminder.hour, reminder.minute))
                        }
                        Text("現在は設定の保存のみで、通知は届きません。",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedValue(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
