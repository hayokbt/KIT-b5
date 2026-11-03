package com.example.kit_b5.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

private val TipsInk = Color(0xFF162B4C)
private val TipsPurple = Color(0xFF8065D8)

@Composable
fun TipsScreen(viewModel: TipsViewModel = viewModel()) {
    val expanded by viewModel.expandedIds.collectAsState()
    Column(Modifier.fillMaxSize().background(Color(0xFFF7F6FC))) {
        Column(Modifier.fillMaxWidth().background(Brush.linearGradient(
            listOf(Color(0xFFEAE3FF), Color(0xFFFFEEE0), Color(0xFFF7F6FC))))
            .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("LEARN ABOUT ACP", color = Color(0xFF6E7B99), fontSize = 12.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("ACPの豆知識", color = TipsInk, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text("人生会議を身近に感じるための豆知識です。\n${viewModel.tips.size}個のTipsを紹介します。",
                color = Color(0xFF7182A0), fontSize = 14.sp, lineHeight = 23.sp)
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.tips, key = { it.id }) { tip ->
                val isExpanded = tip.id in expanded
                Surface(onClick = { viewModel.toggle(tip.id) },
                    modifier = Modifier.fillMaxWidth().semantics {
                        stateDescription = if (isExpanded) "本文を表示中" else "本文を折りたたみ中"
                    },
                    shape = RoundedCornerShape(18.dp), color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E2EF)),
                    shadowElevation = 1.dp) {
                    Row(Modifier.animateContentSize().padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(40.dp).background(Color(0xFFEAE3FF), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center) {
                            Text(String.format(Locale.JAPAN, "%02d", tip.id), color = TipsPurple,
                                fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(tip.title, color = TipsInk, fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, lineHeight = 21.sp)
                            Text(tip.body, color = Color(0xFF70819D), fontSize = 13.sp, lineHeight = 21.sp,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                overflow = TextOverflow.Ellipsis)
                        }
                        Text(if (isExpanded) "⌄" else "›", color = TipsPurple, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            item(key = "sources") {
                Column(Modifier.fillMaxWidth().padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("主な出典・参考文献", color = TipsInk, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp)
                    viewModel.sources.forEachIndexed { index, source ->
                        Text("${index + 1}. $source", color = Color(0xFF70819D),
                            fontSize = 12.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
