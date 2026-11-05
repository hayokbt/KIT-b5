package com.example.kit_b5.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsAvatar(nickname: String, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, Color.White), shadowElevation = 6.dp,
        modifier = Modifier.size(48.dp).semantics { contentDescription = "設定を開く" }) {
        Box(Modifier.background(Brush.linearGradient(listOf(Color(0xFFF7C7A6), Color(0xFFD99371)))),
            contentAlignment = Alignment.Center) {
            Text(nickname.take(1).ifEmpty { "わ" }, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
