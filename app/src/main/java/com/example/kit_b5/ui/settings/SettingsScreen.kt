package com.example.kit_b5.ui.settings

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Locale

private val SettingsPurple = Color(0xFF8065D8)
private val SettingsInk = Color(0xFF172B4D)
private val SettingsMuted = Color(0xFF71809A)

@Composable
fun SettingsScreen(model: SettingsViewModel, onClose: () -> Unit) {
    val context = LocalContext.current
    var yearsOpen by remember { mutableStateOf(false) }
    var datesOpen by remember { mutableStateOf(false) }
    val profile = model.profile
    val reminder = model.reminder
    val year = remember { Calendar.getInstance().get(Calendar.YEAR) }
    MaterialTheme(colorScheme = lightColorScheme(primary = SettingsPurple, onSurface = SettingsInk,
        surface = Color.White, background = Color(0xFFF7F6FC))) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(.94f).imePadding()
            .padding(horizontal = 20.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("設定", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onClose, enabled = !model.saving,
                    modifier = Modifier.semantics { contentDescription = "設定を閉じる" }) { Text("×", fontSize = 24.sp) }
            }
            if (!model.loaded) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (model.error == null) CircularProgressIndicator()
                    else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(model.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = model::load) { Text("再試行") }
                    }
                }
            } else {
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .testTag("settings_scroll"), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SettingsLabel("プロフィール")
                    SettingsLabel("ニックネーム")
                    OutlinedTextField(profile.nickname,
                        onValueChange = { model.updateProfile(profile.copy(nickname = it)) },
                        singleLine = true, enabled = !model.saving,
                        modifier = Modifier.fillMaxWidth().testTag("settings_nickname"),
                        shape = RoundedCornerShape(18.dp),
                        isError = profile.nickname.isBlank(),
                        supportingText = if (profile.nickname.isBlank()) ({ Text("ニックネームを入力してください") }) else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                    SettingsLabel("生まれ年（任意）")
                    Box {
                        OutlinedButton(onClick = { yearsOpen = true }, enabled = !model.saving,
                            shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFFE1DCEC)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = SettingsInk),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) {
                            Text(profile.birthYear?.let { "${it}年" } ?: "答えない", modifier = Modifier.weight(1f))
                            Text("⌄")
                        }
                        DropdownMenu(expanded = yearsOpen, onDismissRequest = { yearsOpen = false },
                            modifier = Modifier.heightIn(max = 320.dp)) {
                            DropdownMenuItem(text = { Text("答えない") }, onClick = {
                                model.updateProfile(profile.copy(birthYear = null)); yearsOpen = false
                            })
                            (year downTo year - 120).forEach { value ->
                                DropdownMenuItem(text = { Text("${value}年") }, onClick = {
                                    model.updateProfile(profile.copy(birthYear = value)); yearsOpen = false
                                })
                            }
                        }
                    }
                    SettingsLabel("性別（任意・もう一度押すと外せます）")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("男性", "女性", "その他").forEach { gender ->
                            FilterChip(selected = profile.gender == gender, enabled = !model.saving,
                                onClick = { model.updateProfile(profile.copy(gender = if (profile.gender == gender) null else gender)) },
                                label = { Text(gender) }, modifier = Modifier.weight(1f))
                        }
                    }
                    Surface(color = Color(0xFFF2EFF8), shape = RoundedCornerShape(16.dp)) {
                        Text("生まれ年は、ホームの「今日のTips」と「今日できること」を年代に合わせて選ぶのに使います。性別は、今は設定画面に表示するだけです。どちらも入力しなくても、すべての機能を使えます。端末の外には送りません。",
                            color = SettingsMuted, fontSize = 12.sp, lineHeight = 20.sp, modifier = Modifier.padding(14.dp))
                    }
                    SettingsLabel("リマインド")
                    Surface(color = Color.White, shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFE6E1F0)), shadowElevation = 1.dp) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("リマインドを受け取る", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("考えるきっかけを通知でお届けします", color = SettingsMuted, fontSize = 12.sp)
                            }
                            Switch(checked = reminder.enabled, enabled = !model.saving,
                                onCheckedChange = { model.updateReminder(reminder.copy(enabled = it)) })
                        }
                    }
                    if (reminder.enabled) {
                        Surface(color = Color.White, shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, Color(0xFFE6E1F0))) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SettingsLabel("頻度")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("毎日", "毎週", "毎月").forEach { frequency ->
                                        FilterChip(selected = reminder.frequency == frequency, enabled = !model.saving,
                                            onClick = { model.updateReminder(reminder.copy(frequency = frequency)) },
                                            label = { Text(frequency) })
                                    }
                                }
                                if (reminder.frequency == "毎週") {
                                    SettingsLabel("曜日")
                                    Row {
                                        listOf("日", "月", "火", "水", "木", "金", "土").forEachIndexed { day, label ->
                                            TextButton(onClick = { model.updateReminder(reminder.copy(day = day)) },
                                                enabled = !model.saving, modifier = Modifier.weight(1f),
                                                contentPadding = PaddingValues(0.dp),
                                                colors = ButtonDefaults.textButtonColors(
                                                    containerColor = if (reminder.day == day) SettingsPurple else Color(0xFFF3F0FA),
                                                    contentColor = if (reminder.day == day) Color.White else SettingsInk)) { Text(label) }
                                        }
                                    }
                                }
                                if (reminder.frequency == "毎月") {
                                    SettingsLabel("日付")
                                    Box {
                                        OutlinedButton(onClick = { datesOpen = true }, enabled = !model.saving) {
                                            Text("${reminder.monthDay}日")
                                        }
                                        DropdownMenu(expanded = datesOpen, onDismissRequest = { datesOpen = false },
                                            modifier = Modifier.heightIn(max = 280.dp)) {
                                            (1..31).forEach { day ->
                                                DropdownMenuItem(text = { Text("${day}日") }, onClick = {
                                                    model.updateReminder(reminder.copy(monthDay = day)); datesOpen = false
                                                })
                                            }
                                        }
                                    }
                                }
                                SettingsLabel("時刻")
                                OutlinedButton(onClick = {
                                    TimePickerDialog(context, { _, hour, minute ->
                                        model.updateReminder(model.reminder.copy(hour = hour, minute = minute))
                                    }, reminder.hour, reminder.minute, true).show()
                                }, enabled = !model.saving) {
                                    Text(String.format(Locale.JAPAN, "%02d:%02d", reminder.hour, reminder.minute))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
            model.error?.takeIf { model.loaded }?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            // スクロールする入力欄から独立させ、保存操作を常に表示する。
            Button(onClick = { model.save(onClose) },
                enabled = model.loaded && !model.saving && model.profile.nickname.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SettingsPurple),
                shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("settings_save")) {
                Text(if (model.saving) "保存中…" else "保存する", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = {
                Toast.makeText(context, "通知機能は準備中です", Toast.LENGTH_SHORT).show()
            }, enabled = !model.saving, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)) {
                Text("♧  通知を試す", color = SettingsInk)
            }
        }
    }
}

@Composable
private fun SettingsLabel(text: String) {
    Text(text, color = SettingsMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
}
