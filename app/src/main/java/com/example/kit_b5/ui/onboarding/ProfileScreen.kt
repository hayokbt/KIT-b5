package com.example.kit_b5.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onNext: () -> Unit, onBack: () -> Unit) {
    val profile = viewModel.profile
    var yearsOpen by remember { mutableStateOf(false) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    OnboardingFrame(1) {
        Spacer(Modifier.height(12.dp))
        Text("STEP 1", color = Purple, letterSpacing = 3.sp)
        Text("なんとお呼びすれば\nいいですか？", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("アプリの中でお呼びする名前です。")
        Text("ニックネーム")
        OutlinedTextField(value = profile.nickname,
            onValueChange = { viewModel.update(profile.copy(nickname = it)) },
            placeholder = { Text("アプリ内での呼び名") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
        Text("生まれ年 （任意）")
        Box {
            OutlinedButton(onClick = { yearsOpen = true }, modifier = Modifier.fillMaxWidth()) {
                Text(profile.birthYear?.let { "${it}年" } ?: "答えない")
            }
            DropdownMenu(expanded = yearsOpen, onDismissRequest = { yearsOpen = false },
                modifier = Modifier.heightIn(max = 300.dp)) {
                DropdownMenuItem(text = { Text("答えない") }, onClick = {
                    viewModel.update(profile.copy(birthYear = null)); yearsOpen = false
                })
                (currentYear downTo currentYear - 120).forEach { year ->
                    DropdownMenuItem(text = { Text("${year}年") }, onClick = {
                        viewModel.update(profile.copy(birthYear = year)); yearsOpen = false
                    })
                }
            }
        }
        Text("性別 （任意・もう一度押すと外せます）", fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("男性", "女性", "その他").forEach { gender ->
                FilterChip(selected = profile.gender == gender,
                    onClick = { viewModel.update(profile.copy(gender = if (profile.gender == gender) null else gender)) },
                    label = { Text(gender) }, modifier = Modifier.weight(1f))
            }
        }
        Text("生まれ年は、ホームの「今日のTips」と「今日できること」を年代に合わせて選ぶのに使います。性別は、今は設定画面に表示するだけです。どちらも入力しなくても、すべての機能を使えます。端末の外には送りません。",
            fontSize = 12.sp, lineHeight = 20.sp)
        viewModel.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.weight(1f).heightIn(min = 16.dp))
        PrimaryAction("次へ", profile.nickname.isNotBlank() && !viewModel.saving) { viewModel.next(onNext) }
        OutlinedButton(onClick = onBack, enabled = !viewModel.saving, modifier = Modifier.fillMaxWidth()) { Text("戻る") }
    }
}
