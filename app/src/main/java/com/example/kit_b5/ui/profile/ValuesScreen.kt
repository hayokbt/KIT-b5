package com.example.kit_b5.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val ValuesInk = Color(0xFF172B4D)
private val ValuesPurple = Color(0xFF8065D8)

@Composable
fun ValuesScreen(model: ValuesViewModel = viewModel()) {
    val state by model.state.collectAsState()
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("正解はありません。今の気持ちに近いほうを選んでください。回答はこの端末に保存されます。",
            color = Color(0xFF71809A), fontSize = 12.sp, lineHeight = 20.sp)
        model.saveError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.error -> Column {
                Text("質問を読み込めませんでした。")
                TextButton(onClick = model::retry) { Text("再試行") }
            }
            else -> LazyColumn(Modifier.weight(1f).testTag("value_questions"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)) {
                item(key = "heading") {
                    Text("価値観分析", color = ValuesInk, fontWeight = FontWeight.Bold)
                }
                items(state.questions, key = { it.id }) { question ->
                    Surface(color = Color.White, shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5DFF1)), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("${question.id}. ${question.question}", color = ValuesInk,
                                fontSize = 15.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold)
                            listOf(Triple("A", question.optionA, question.valueCodeA),
                                Triple("B", question.optionB, question.valueCodeB)).forEach { (label, option, code) ->
                                val chosen = question.answerCode == code
                                Surface(onClick = { model.answer(question, code) },
                                    enabled = question.id !in model.savingIds,
                                    color = if (chosen) Color(0xFFF0E9FF) else Color(0xFFFCFAFF),
                                    border = BorderStroke(1.dp, if (chosen) ValuesPurple else Color(0xFFDFD6EF)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("value_${question.id}_$label")
                                        .semantics { selected = chosen }) {
                                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Surface(color = Color(0xFFEFE8FB), shape = RoundedCornerShape(10.dp)) {
                                            Text(label, color = ValuesPurple, fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                        }
                                        Text(option, color = ValuesInk, modifier = Modifier.weight(1f),
                                            fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium)
                                        if (chosen) Text("✓", color = ValuesPurple)
                                    }
                                }
                            }
                            if (question.id in model.savingIds) Text("保存中…", fontSize = 11.sp, color = ValuesPurple)
                        }
                    }
                }
                item(key = "footer") {
                    Text("選択した回答は自動で保存されます。いつでも選び直せます。",
                        color = Color(0xFF71809A), fontSize = 12.sp)
                }
            }
        }
    }
}
