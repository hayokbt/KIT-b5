package com.example.kit_b5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppTab(val label: String, val symbol: String) {
    Home("ホーム", "⌂"), Tips("Tips", "♧"), Guide("ガイド", "▤"),
    Simulation("人生体験", "⌛"), Profile("わたし", "♙")
}

@Composable
fun AppBottomBar(selected: AppTab, onSelect: (AppTab) -> Unit) {
    Surface(shape = RoundedCornerShape(28.dp), color = Color.White.copy(alpha = .97f),
        shadowElevation = 12.dp, modifier = Modifier.navigationBarsPadding().padding(12.dp)) {
        Row(Modifier.fillMaxWidth().padding(6.dp)) {
            AppTab.entries.forEach { tab ->
                val active = selected == tab
                Column(Modifier.weight(1f).background(if (active) Color(0xFF8D75DF) else Color.Transparent,
                    RoundedCornerShape(22.dp)).clickable { onSelect(tab) }.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(tab.symbol, fontSize = 23.sp, color = if (active) Color.White else Color(0xFF72778C))
                    Text(tab.label, fontSize = 11.sp, color = if (active) Color.White else Color(0xFF72778C))
                }
            }
        }
    }
}
