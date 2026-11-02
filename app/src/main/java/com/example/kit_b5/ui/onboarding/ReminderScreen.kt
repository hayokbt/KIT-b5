package com.example.kit_b5.ui.onboarding

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReminderScreen(viewModel: ReminderViewModel) {
    val settings = viewModel.settings
    val context = LocalContext.current
    OnboardingFrame(2) {
        Spacer(Modifier.height(12.dp))
        Text("STEP 2", color = Purple, letterSpacing = 3.sp)
        Text("${viewModel.nickname}さん、\nときどきお知らせしますね",
            fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("気持ちは時間とともに変わるもの。定期的に振り返るきっかけをお届けします。")
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text("リマインドを受け取る", fontWeight = FontWeight.Bold)
                    Text("考えるきっかけを通知でお届けします", fontSize = 12.sp)
                }
                Switch(checked = settings.enabled, onCheckedChange = { viewModel.update(settings.copy(enabled = it)) })
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("頻度")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("毎日", "毎週", "毎月").forEach { frequency ->
                        FilterChip(selected = settings.frequency == frequency, enabled = settings.enabled,
                            onClick = { viewModel.update(settings.copy(frequency = frequency)) }, label = { Text(frequency) })
                    }
                }
                if (settings.frequency == "毎週") {
                    Text("曜日")
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        listOf("日", "月", "火", "水", "木", "金", "土").forEachIndexed { index, day ->
                            Box(Modifier.weight(1f)) {
                                TextButton(enabled = settings.enabled,
                                    onClick = { viewModel.update(settings.copy(day = index)) },
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = if (settings.day == index) Purple else MaterialTheme.colorScheme.surface,
                                        contentColor = if (settings.day == index) MaterialTheme.colorScheme.onPrimary else Ink),
                                    modifier = Modifier.fillMaxWidth()) { Text(day) }
                            }
                        }
                    }
                }
                if (settings.frequency == "毎月") {
                    Text("日付（29日以降を選ぶ場合、短い月は月末を想定）")
                    Row {
                        TextButton(enabled = settings.enabled, onClick = {
                            viewModel.update(settings.copy(monthDay = if (settings.monthDay == 1) 31 else settings.monthDay - 1))
                        }) { Text("−") }
                        Text("${settings.monthDay}日", modifier = Modifier.padding(12.dp))
                        TextButton(enabled = settings.enabled, onClick = {
                            viewModel.update(settings.copy(monthDay = if (settings.monthDay == 31) 1 else settings.monthDay + 1))
                        }) { Text("＋") }
                    }
                }
                Text("時刻")
                OutlinedButton(enabled = settings.enabled, onClick = {
                    TimePickerDialog(context, { _, hour, minute ->
                        viewModel.update(settings.copy(hour = hour, minute = minute))
                    }, settings.hour, settings.minute, true).show()
                }) { Text("%02d:%02d".format(settings.hour, settings.minute)) }
            }
        }
        viewModel.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.weight(1f).heightIn(min = 16.dp))
        PrimaryAction("この設定で始める", !viewModel.saving) { viewModel.finish(true) }
        OutlinedButton(onClick = { viewModel.finish(false) }, enabled = !viewModel.saving,
            modifier = Modifier.fillMaxWidth()) { Text("リマインドなしではじめる") }
    }
}
