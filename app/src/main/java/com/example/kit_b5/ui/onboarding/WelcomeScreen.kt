package com.example.kit_b5.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val Purple = Color(0xFF8065D8)
internal val Ink = Color(0xFF152744)

@Composable
internal fun OnboardingFrame(step: Int, content: @Composable ColumnScope.() -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Purple, onPrimary = Color.White,
        background = Color(0xFFF7F6FC), surface = Color.White, onSurface = Ink)) {
        BoxWithConstraints(Modifier.fillMaxSize().background(Brush.linearGradient(
            listOf(Color(0xFFEAE3FF), Color(0xFFFFEBDD), Color(0xFFF7F6FC)))))
        {
            Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding()
                .padding(12.dp).background(Color(0xFFF8F7FC).copy(alpha = .88f), RoundedCornerShape(32.dp))
                .padding(24.dp).verticalScroll(rememberScrollState()).heightIn(min = (maxHeight - 120.dp).coerceAtLeast(0.dp)),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) { index ->
                        Box(Modifier.size(if (index == step) 32.dp else 16.dp, 6.dp)
                            .background(if (index == step) Purple else Purple.copy(alpha = .2f), RoundedCornerShape(3.dp)))
                    }
                }
                content()
            }
        }
    }
}

@Composable
internal fun PrimaryAction(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
        shape = RoundedCornerShape(16.dp)) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel, onStart: (Int) -> Unit) {
    OnboardingFrame(0) {
        Spacer(Modifier.height(120.dp))
        Surface(color = Purple, shape = RoundedCornerShape(30.dp),
            shadowElevation = 18.dp, modifier = Modifier.size(96.dp).align(Alignment.CenterHorizontally)) {
            Box(contentAlignment = Alignment.Center) { Text("♡", fontSize = 52.sp, color = Color.White) }
        }
        Spacer(Modifier.height(36.dp))
        Text("WELCOME", color = Purple, letterSpacing = 3.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text("あなたらしい未来を、\n一緒に考えていきましょう",
            fontSize = 25.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        Text("人生会議（ACP）は、もしものときに大切にしたいことを、前もって考え、伝えておくこと。少しずつで大丈夫です。",
            color = Color(0xFF687C9A), textAlign = TextAlign.Center, lineHeight = 26.sp)
        Spacer(Modifier.weight(1f).heightIn(min = 64.dp))
        PrimaryAction("はじめる") { onStart(viewModel.nextStep()) }
    }
}
