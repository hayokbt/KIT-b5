package com.example.kit_b5.ui.guide

import com.example.kit_b5.ui.settings.SettingsAvatar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val GuideInk = Color(0xFF172B4D)
private val GuidePurple = Color(0xFF8065D8)
private val GuideMuted = Color(0xFF71809A)

private val questionGroups = listOf(
    "はじめる" to listOf("人生会議は、何から始めればいい？", "ACPで決めることを教えて", "まだ元気なのに、考える必要はある？"),
    "家族と話す" to listOf("家族にどう切り出せばいい？", "家族と意見が違ったら？", "親の希望を聞いてみたいときは？"),
    "医療のこと" to listOf("延命治療って、どういうこと？", "代わりに決める人は、どう選べばいい？", "自宅で最期を過ごすには、何が必要？"),
    "書き残す・見直す" to listOf("決めたことは、書き残したほうがいい？", "一度決めたら、変えられない？")
)

@Composable
fun GuideScreen(nickname: String = "", model: GuideViewModel = viewModel(), onOpenSettings: () -> Unit = {}) {
    val draft by model.draft.collectAsState()
    val questions by model.questions.collectAsState()
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    val uriHandler = LocalUriHandler.current
    fun sendDraft() { model.sendDraft(); keyboard?.hide() }
    LaunchedEffect(questions.size) {
        if (questions.isNotEmpty()) listState.animateScrollToItem(3 + questionGroups.size + questions.lastIndex)
    }
    Column(Modifier.fillMaxSize().background(Brush.linearGradient(
        listOf(Color(0xFFECE5FF), Color(0xFFFFEEE1), Color(0xFFF7F6FC)))) .imePadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("QUESTIONS & HINTS", color = GuideMuted, fontSize = 12.sp,
                    letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Text("ACPガイド", color = GuideInk, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            SettingsAvatar(nickname = nickname, onClick = onOpenSettings)
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item(key = "welcome") {
                GuideReply("こんにちは。人生会議や、もしものときのことを、いっしょに考えるガイドです。\n\n下のよくある質問から、気になるものを選んでください。いちばん下の欄に自由に書くこともできますが、AIへの相談はいま準備中のため、まだお答えできません。")
            }
            item(key = "support") {
                Surface(color = Color(0xFFEDF7FB), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("つらい気持ちを、ひとりで抱えないで", color = Color(0xFF24617B),
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("人には話しにくい悩みや、死にたいほどつらい気持ちがあるときは、電話やSNSで相談できる窓口があります。",
                            color = Color(0xFF587B96), fontSize = 12.sp, lineHeight = 21.sp)
                        Text("出典", color = GuideMuted, fontSize = 12.sp)
                        TextButton(onClick = { uriHandler.openUri("https://www.mhlw.go.jp/mamorouyokokoro/") },
                            contentPadding = PaddingValues(0.dp)) {
                            Text("厚生労働省「まもろうよ こころ」\n（困った時の相談方法・窓口）",
                                color = GuidePurple, fontSize = 12.sp, lineHeight = 19.sp)
                        }
                    }
                }
            }
            questionGroups.forEach { (heading, suggestions) ->
                item(key = heading) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(heading, color = GuideMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp))
                        suggestions.forEach { question ->
                            Surface(onClick = { model.send(question); keyboard?.hide() }, color = Color.White,
                                shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Color(0xFFDCD0FC))) {
                                Text(question, color = GuidePurple, fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                            }
                        }
                    }
                }
            }
            item(key = "notice") {
                Surface(color = Color(0xFFF0F0F7), shape = RoundedCornerShape(16.dp)) {
                    Text("ここに出る答えは一般的な情報で、あなたの状況に合わせた判断ではありません。医療や介護の判断は、医師や医療・ケアチームにご相談ください。",
                        color = GuideMuted, fontSize = 12.sp, lineHeight = 20.sp, modifier = Modifier.padding(14.dp))
                }
            }
            itemsIndexed(questions, key = { index, _ -> "exchange_$index" }) { _, question ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = Color(0xFFEAE2FC), shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.align(Alignment.End).widthIn(max = 320.dp)) {
                        Text(question, color = GuideInk, fontSize = 14.sp,
                            lineHeight = 24.sp, modifier = Modifier.padding(16.dp))
                    }
                    GuideReply(GuideViewModel.PLACEHOLDER_REPLY)
                }
            }
        }
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White,
            shadowElevation = 8.dp, border = BorderStroke(1.dp, Color(0xFFE4DEF1)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp)) {
            Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(value = draft, onValueChange = model::updateDraft,
                    placeholder = { Text("相談したいことを書く（AIは準備中です）", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f), maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { sendDraft() }),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = GuideInk, unfocusedTextColor = GuideInk))
                FilledIconButton(onClick = { sendDraft() }, enabled = draft.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = GuidePurple),
                    modifier = Modifier.size(48.dp).semantics { contentDescription = "送信" }) {
                    Text("↑", color = Color.White, fontSize = 23.sp)
                }
            }
        }
    }
}

@Composable
private fun GuideReply(message: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Surface(color = GuidePurple, shape = RoundedCornerShape(12.dp)) {
            Text("▤", color = Color.White, modifier = Modifier.padding(9.dp))
        }
        Surface(color = Color.White, shadowElevation = 2.dp, shape = RoundedCornerShape(18.dp),
            modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite }) {
            Text(message, color = GuideInk, fontSize = 14.sp, lineHeight = 25.sp,
                modifier = Modifier.padding(16.dp))
        }
    }
}
