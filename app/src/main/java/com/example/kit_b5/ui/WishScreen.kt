package com.example.kit_b5.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val WishPurple = Color(0xFF8065D8)
private val WishInk = Color(0xFF172B4D)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WishScreen(onClose: () -> Unit, model: WishViewModel = viewModel()) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var value by rememberSaveable { mutableStateOf("") }
    val wish = model.wish
    Surface(color = Color(0xFFF7F6FC), contentColor = WishInk) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("MY WISH", color = WishPurple, letterSpacing = 2.sp, fontSize = 12.sp)
                    Text("わたしの希望", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                TextButton(onClick = onClose, enabled = !model.saving) { Text("閉じる", color = WishPurple) }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("希望を整理する", "価値観分析").forEachIndexed { index, label ->
                    FilterChip(selected = tab == index, onClick = { tab = index },
                        label = { Text(label) }, modifier = Modifier.weight(1f))
                }
            }
            if (tab == 1) {
                Box(Modifier.weight(1f)) { ValuesScreen() }
            } else if (!model.loaded) {
                model.message?.let {
                    Text(it)
                    TextButton(onClick = model::load) { Text("再試行") }
                } ?: CircularProgressIndicator()
            } else {
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("7つの項目から、今の希望を整理できます。全部を一度に決めなくて大丈夫です。",
                        color = Color(0xFF71809A), fontSize = 12.sp, lineHeight = 20.sp)
                    WishSection("大切にしたいこと", "もしものときも大切にしたいことを、5つまで。") {
                        wish.values.forEach { item ->
                            Row {
                                Text(item, modifier = Modifier.weight(1f).padding(8.dp))
                                TextButton(onClick = { model.update(wish.copy(values = wish.values - item)) }) { Text("削除") }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value, { value = it }, placeholder = { Text("例：家族と話せること") },
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), singleLine = true)
                            Button(enabled = value.isNotBlank() && wish.values.size < 5 && value.trim() !in wish.values,
                                onClick = { model.update(wish.copy(values = wish.values + value.trim())); value = "" }) { Text("足す") }
                        }
                    }
                    WishSection("過ごしたい場所", "人生の最期を、どこで過ごしたいですか。") {
                        listOf("住み慣れた家", "病院", "介護施設", "緩和ケア病棟（ホスピス）", "まだ決めていない").forEach {
                            WishOption(it, wish.place == it) { model.update(wish.copy(place = if (wish.place == it) "" else it)) }
                        }
                    }
                    WishSection("代わりに決めてほしい人", "自分で伝えられないとき、医療者と話してほしい人。家族でなくても構いません。") {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("配偶者・パートナー", "子ども", "きょうだい", "親", "友人", "その他").forEach {
                                FilterChip(selected = it in wish.people, onClick = {
                                    model.update(wish.copy(people = if (it in wish.people) wish.people - it else wish.people + it))
                                }, label = { Text(it) })
                            }
                        }
                    }
                    WishSection("治療の考え方", "今の考えで大丈夫です。もう一度押すと外せます。") {
                        listOf("できる限りの治療を続けたい", "回復の見込みがあるなら治療したい", "苦痛をやわらげることを優先したい").forEach {
                            WishOption(it, wish.treatment == it) { model.update(wish.copy(treatment = if (wish.treatment == it) "" else it)) }
                        }
                    }
                    WishSection("処置ごとの希望", "説明は一般的な情報です。実際の判断は、医師や医療・ケアチームと相談してください。") {
                        listOf("心肺蘇生", "人工呼吸器", "胃ろう・経管栄養", "点滴", "人工透析", "緩和ケア・鎮静").forEach { name ->
                            Surface(color = Color(0xFFFAF8FF), shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFFE5DFF1))) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(name, fontWeight = FontWeight.Bold)
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("望む", "望まない", "わからない").forEach { answer ->
                                            FilterChip(selected = wish.procedures[name] == answer,
                                                onClick = { model.update(wish.copy(procedures = if (wish.procedures[name] == answer)
                                                    wish.procedures - name else wish.procedures + (name to answer))) },
                                                label = { Text(answer, fontSize = 12.sp) })
                                        }
                                    }
                                }
                            }
                        }
                    }
                    WishSection("伝えた相手と日付", "この希望を話した相手を残しておくと、見直すときの手がかりになります。") {
                        Text("今日、伝えた相手を足す", fontSize = 12.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("配偶者・パートナー", "子ども", "きょうだい", "親", "友人", "医療者").forEach { person ->
                                FilterChip(selected = person in wish.shared, onClick = {
                                    model.update(wish.copy(shared = if (person in wish.shared) wish.shared - person
                                        else wish.shared + (person to System.currentTimeMillis())))
                                }, label = { Text("＋ $person") })
                            }
                        }
                        wish.shared.forEach { (person, date) ->
                            Text("$person　" + SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(Date(date)), fontSize = 12.sp)
                        }
                    }
                    WishSection("臓器提供の意思", "") {
                        listOf("提供したい", "提供しない", "まだ決めていない").forEach {
                            WishOption(it, wish.donation == it) { model.update(wish.copy(donation = if (wish.donation == it) "" else it)) }
                        }
                    }
                    Text("書いたことは端末の中だけに保存されます。気持ちが変わったら、いつでも書き直せます。",
                        fontSize = 12.sp, color = Color(0xFF71809A), lineHeight = 20.sp)
                    model.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(onClick = { model.save(onClose) }, enabled = !model.saving,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp), shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WishPurple)) {
                        Text(if (model.saving) "保存中…" else "保存する")
                    }
                }
            }
        }
    }
}

@Composable
private fun WishSection(title: String, description: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(24.dp), shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color(0xFFEAE5F3))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WishInk)
            if (description.isNotEmpty()) Text(description, fontSize = 12.sp, color = Color(0xFF71809A), lineHeight = 20.sp)
            content()
        }
    }
}
@Composable
private fun WishOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(17.dp),
        color = if (selected) Color(0xFFF2EDFF) else Color.White,
        border = BorderStroke(1.dp, if (selected) WishPurple else Color(0xFFE1DCEC)),
        modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WishInk, modifier = Modifier.padding(16.dp))
    }
}
