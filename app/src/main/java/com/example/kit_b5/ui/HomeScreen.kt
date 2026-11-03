package com.example.kit_b5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HomeInk = Color(0xFF172B4D)
private val HomePurple = Color(0xFF826AD2)

@Composable
fun HomeScreen(viewModel: SavedInformationViewModel, onEditReminder: () -> Unit, onNavigate: (AppTab) -> Unit) {
    val state by viewModel.state.collectAsState()
    val nickname = state.profile.nickname.ifBlank { "あなた" }
    val background = Brush.linearGradient(listOf(Color(0xFFECE5FF), Color(0xFFFFEEE1), Color(0xFFF7F7FC)))
    Column(Modifier.fillMaxSize().background(background).safeDrawingPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("GOOD EVENING", color = Color(0xFF71809A), fontSize = 12.sp, letterSpacing = 3.sp)
                Text("こんにちは、${nickname}さん", color = HomeInk, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
            Box(Modifier.size(48.dp).background(Brush.linearGradient(listOf(Color(0xFFF7C7A6), Color(0xFFD99371))),
                RoundedCornerShape(18.dp)).border(2.dp, Color.White, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center) {
                Text(nickname.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (state.error) Text("保存した情報を読み込めませんでした。", color = HomeInk)
            val reminder = state.reminder
            val schedule = if (reminder?.enabled == true) {
                val frequency = when (reminder.frequency) {
                    "毎週" -> "毎週" + listOf("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日").getOrElse(reminder.day) { "" }
                    "毎月" -> "毎月${reminder.monthDay}日"
                    else -> "毎日"
                }
                "%s %02d:%02d".format(java.util.Locale.JAPAN, frequency, reminder.hour, reminder.minute)
            } else ""
            HomeTile("♧", "リマインド", if (state.loading) "読み込み中…" else if (reminder?.enabled == true) "オン" else "オフ",
                schedule, Color.White, trailing = "変更", onTrailingClick = onEditReminder)
            Surface(shape = RoundedCornerShape(24.dp), color = HomePurple, shadowElevation = 8.dp) {
                Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("MY ACP JOURNEY", color = Color.White, fontSize = 12.sp, letterSpacing = 2.sp)
                    Text("あなたらしい未来を、\n少しずつ考えてみよう", color = Color.White,
                        fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("今月の記録", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                            Text("今月はまだ記録がありません", color = Color.White, fontSize = 14.sp)
                        }
                        Box(Modifier.size(54.dp).border(6.dp, Color.White.copy(alpha = .25f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center) { Text("0%", color = Color.White) }
                    }
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text("○ リマインド\n○ 写真の振り返り\n○ 希望を書く", color = Color.White.copy(alpha = .8f), fontSize = 12.sp, lineHeight = 23.sp)
                        }
                        Text("○ クイズ\n○ 大切なこと 0/3\n○ 希望を伝える", color = Color.White.copy(alpha = .8f), fontSize = 12.sp, lineHeight = 23.sp)
                    }
                    Surface(color = Color.White.copy(alpha = .2f), shape = RoundedCornerShape(18.dp)) {
                        Text("次の一歩：リマインドを設定する　 ›", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(14.dp))
                    }
                }
            }
            HomeTile("▤", "ACPガイド", "よくある質問からヒントを見る", "家族への切り出し方や、決めることを紹介", Color(0xFFF6EEFC), onClick = { onNavigate(AppTab.Guide) })
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("今日のTips", color = HomeInk, fontWeight = FontWeight.Bold)
                Text("すべて見る ›", color = HomePurple, fontSize = 12.sp)
            }
            HomeTile("▥", "話し合いのコツ・1分で読める", "医療者にも話してよい", "かかりつけ医・看護師も大切な相手", Color(0xFFECF8F2), onClick = { onNavigate(AppTab.Tips) })
            Text("今日できること", color = HomeInk, fontWeight = FontWeight.Bold)
            HomeTile("?", "ACP QUIZ", "クイズで人生会議を学ぼう", "問題に答えながら、医療・ケアや人生会議の知識を深める",
                Color(0xFF626590), light = true, onClick = { onNavigate(AppTab.Quiz) })
            HomeTile("▧", "MEMORY ALBUM", "大切な人との写真を残す", "端末の中だけに保存されます", Color.White, onClick = { onNavigate(AppTab.Profile) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HomeTile(symbol: String, label: String, title: String, subtitle: String,
    background: Color, light: Boolean = false, trailing: String = "›", onTrailingClick: (() -> Unit)? = null, onClick: (() -> Unit)? = null) {
    val ink = if (light) Color.White else HomeInk
    Surface(color = background, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(HomePurple.copy(alpha = .18f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center) { Text(symbol, color = ink, fontSize = 26.sp) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, color = if (light) Color.White else HomePurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(title, color = ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (subtitle.isNotEmpty()) Text(subtitle, color = ink.copy(alpha = .7f), fontSize = 12.sp)
            }
            if (onTrailingClick != null) {
                TextButton(onClick = onTrailingClick) { Text(trailing, color = HomePurple) }
            } else Text(trailing, color = if (light) Color.White else HomePurple, fontSize = 14.sp)
        }
    }
}
