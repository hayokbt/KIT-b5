package com.example.kit_b5.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val QuizInk = Color(0xFF172B4D)
private val QuizPurple = Color(0xFF8065D8)
private val QuizGreen = Color(0xFF24796F)
private val QuizRed = Color(0xFFB54455)

@Composable
fun QuizScreen(viewModel: QuizViewModel = viewModel()) {
    val progress = viewModel.progress
    var showRestartConfirmation by rememberSaveable { mutableStateOf(false) }
    if (showRestartConfirmation) {
        AlertDialog(
            onDismissRequest = { showRestartConfirmation = false },
            title = { Text("はじめからやり直しますか？") },
            text = { Text("これまでの回答と正答率をリセットして、最初の問題に戻ります。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.restart()
                    showRestartConfirmation = false
                }) { Text("やり直す", color = QuizPurple) }
            },
            dismissButton = {
                TextButton(onClick = { showRestartConfirmation = false }) { Text("続ける", color = QuizInk) }
            },
            containerColor = Color(0xFFFCFAFF),
            shape = RoundedCornerShape(24.dp)
        )
    }
    Column(Modifier.fillMaxSize().background(Brush.linearGradient(
        listOf(Color(0xFFECE5FF), Color(0xFFFFEEE1), Color(0xFFF7F6FC))))) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("LEARN WITH QUIZ", color = Color(0xFF71809A), fontSize = 12.sp,
                    letterSpacing = 2.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { showRestartConfirmation = true },
                    enabled = progress.answers.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = QuizPurple)
                ) {
                    Text("やり直す", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            Text("人生会議クイズ", color = QuizInk, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(color = Color.White.copy(alpha = .8f), shape = RoundedCornerShape(16.dp)) {
                    Text(if (progress.finished) "完了" else viewModel.question.level.label,
                        color = QuizPurple, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                }
                Text("正答率 " + (viewModel.accuracy?.let { "$it%" } ?: "—"),
                    color = QuizPurple, fontSize = 13.sp)
            }
            LinearProgressIndicator(progress = { progress.answers.size.toFloat() / viewModel.questions.size },
                modifier = Modifier.fillMaxWidth().height(5.dp), color = QuizPurple,
                trackColor = Color(0xFFE8E3F0))
        }
        key(progress.index, progress.finished) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (progress.finished) {
                    Surface(shape = RoundedCornerShape(24.dp), color = Color.White,
                        modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("おつかれさまでした！", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = QuizInk)
                            Text("すべてのクイズに回答しました。", color = QuizInk)
                            Text("正解 ${viewModel.correctCount} / ${viewModel.questions.size}",
                                color = QuizPurple, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    QuizAction("もう一度はじめる", viewModel::restart)
                } else {
                    val question = viewModel.question
                    val selected = viewModel.selectedAnswer
                    Surface(shape = RoundedCornerShape(26.dp), color = Color.White,
                        shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("問題", color = QuizPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(question.prompt, color = QuizInk, fontSize = 19.sp,
                                lineHeight = 30.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            question.choices.forEachIndexed { index, choice ->
                                val showCorrect = selected != null && index == question.correctIndex
                                val showWrong = selected == index && index != question.correctIndex
                                val accent = when {
                                    showCorrect -> QuizGreen
                                    showWrong -> QuizRed
                                    else -> QuizPurple
                                }
                                Surface(onClick = { viewModel.answer(index) }, enabled = selected == null,
                                    shape = RoundedCornerShape(18.dp),
                                    color = when {
                                        showCorrect -> Color(0xFFEFFAF5)
                                        showWrong -> Color(0xFFFFF0F2)
                                        else -> Color(0xFFFCFAFF)
                                    },
                                    border = BorderStroke(1.dp, if (showCorrect || showWrong) accent.copy(alpha = .6f)
                                        else Color(0xFFDFD6F5)), modifier = Modifier.fillMaxWidth()) {
                                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Surface(color = accent.copy(alpha = .1f), shape = RoundedCornerShape(12.dp)) {
                                            Text(if (index == 0) "A" else "B", color = accent,
                                                fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                                        }
                                        Text(choice, color = QuizInk, fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp, lineHeight = 23.sp, modifier = Modifier.weight(1f))
                                        if (showCorrect || showWrong) Text(if (showCorrect) "✓" else "×",
                                            color = accent, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    if (selected != null) {
                        val correct = selected == question.correctIndex
                        Surface(color = if (correct) Color(0xFFE9F7F1) else Color(0xFFFFEEF0),
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(if (correct) "✓ 正解です" else "× 不正解です",
                                    color = if (correct) QuizGreen else QuizRed, fontWeight = FontWeight.Bold)
                                Text("正解：" + (if (question.correctIndex == 0) "A" else "B") +
                                    "　" + question.choices[question.correctIndex], color = QuizInk,
                                    fontWeight = FontWeight.Bold)
                                Text("解説", color = QuizGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(question.explanation, color = QuizInk, fontSize = 14.sp, lineHeight = 25.sp)
                            }
                        }
                        QuizAction(if (progress.index == viewModel.questions.lastIndex) "結果を見る" else "次の問題へ",
                            viewModel::next)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizAction(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = QuizPurple),
        shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
