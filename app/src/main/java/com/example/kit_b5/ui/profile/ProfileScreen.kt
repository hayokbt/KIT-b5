package com.example.kit_b5.ui.profile

import com.example.kit_b5.ui.settings.SettingsAvatar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kit_b5.data.Room.MemoryPhotoInfo
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ProfileInk = Color(0xFF172B4D)
private val ProfilePurple = Color(0xFF8369D6)

@Composable
fun ProfileScreen(nickname: String = "", viewModel: ProfileViewModel = viewModel(), onOpenSettings: () -> Unit = {}) {
    var showWish by rememberSaveable { mutableStateOf(false) }
    if (showWish) {
        androidx.activity.compose.BackHandler { showWish = false }
        WishScreen(onClose = { showWish = false })
        return
    }
    val gallery by viewModel.gallery.collectAsState()
    var pendingDelete by rememberSaveable { mutableStateOf<Long?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::add)
    }
    pendingDelete?.let { id ->
        AlertDialog(onDismissRequest = { pendingDelete = null },
            title = { Text("この写真を削除しますか？") },
            text = { Text("アプリに保存した写真を削除します。端末にある元の写真は削除されません。") },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(id); pendingDelete = null }) { Text("削除", color = Color(0xFFB54455)) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("キャンセル") } },
            containerColor = Color(0xFFFCFAFF), shape = RoundedCornerShape(24.dp))
    }
    Column(Modifier.fillMaxSize().background(Brush.linearGradient(
        listOf(Color(0xFFECE5FF), Color(0xFFFFEEE1), Color(0xFFF7F6FC))))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ABOUT ME", color = Color(0xFF71809A), fontSize = 12.sp,
                    letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Text("わたしのこと", color = ProfileInk, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            SettingsAvatar(nickname = nickname, onClick = onOpenSettings)
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item(key = "wish") {
                Surface(color = Color.White, shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("MY WISH", color = ProfilePurple, letterSpacing = 2.sp, fontSize = 12.sp)
                                Text("わたしの希望", color = ProfileInk, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            }
                            Surface(color = Color(0xFFF2EDFF), shape = RoundedCornerShape(12.dp)) {
                                Text("7つの項目", color = ProfilePurple, fontSize = 12.sp, modifier = Modifier.padding(6.dp))
                            }
                        }
                        Text("人生会議や写真の振り返りで気づいたことを、自分の希望として残しておけます。7つの項目から整理できます。",
                            color = Color(0xFF607797), fontSize = 14.sp, lineHeight = 23.sp)
                        Button(onClick = { showWish = true }, enabled = true, shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = ProfilePurple.copy(alpha = .7f),
                                disabledContentColor = Color.White),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text("♡  希望を書いてみる", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item(key = "add") {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(
                    Brush.linearGradient(listOf(Color(0xFFFF9B90), Color(0xFFF4A7CA), Color(0xFFAA90CF))))) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("MEMORY ALBUM", color = Color.White, letterSpacing = 2.sp, fontSize = 12.sp)
                        Text("大切な人との写真を、\nひとつの場所に", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 29.sp)
                        Text("写真は端末の中だけに保存され、外部には送信されません。",
                            color = Color.White, fontSize = 12.sp, lineHeight = 20.sp)
                        Button(onClick = {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }, enabled = !viewModel.saving,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFD06390)),
                            shape = RoundedCornerShape(24.dp)) {
                            Text(if (viewModel.saving) "保存中…" else "＋ 写真を追加", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            viewModel.error?.let { message ->
                item(key = "actionError") { Text(message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }
            }
            item(key = "galleryTitle") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("保存した写真", color = ProfileInk, fontWeight = FontWeight.Bold)
                    Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
                        Text("${gallery.photos.size}枚", color = Color(0xFF71809A),
                            fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
            when {
                gallery.loading -> item(key = "loading") {
                    Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                gallery.error -> item(key = "loadError") {
                    Column {
                        Text("写真を読み込めませんでした。", color = ProfileInk)
                        TextButton(onClick = viewModel::retry) { Text("再試行") }
                    }
                }
                gallery.photos.isEmpty() -> item(key = "empty") {
                    Surface(color = Color.White.copy(alpha = .8f), shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFDED4EF)), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("▧", color = ProfilePurple, fontSize = 34.sp)
                            Text("まだ保存している画像はありません", color = ProfileInk,
                                fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("「写真を追加」から思い出を残しましょう", color = Color(0xFF71809A), fontSize = 12.sp)
                        }
                    }
                }
                else -> items(gallery.photos.chunked(2), key = { row -> row.first().id }) { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { photo ->
                            PhotoCard(photo, viewModel, photo.id in viewModel.deleting,
                                onDelete = { pendingDelete = photo.id }, modifier = Modifier.weight(1f))
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCard(photo: MemoryPhotoInfo, viewModel: ProfileViewModel, deleting: Boolean,
    onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val thumbnail by produceState<Pair<Boolean, ImageBitmap?>>(true to null, photo.id) {
        value = try { false to viewModel.thumbnail(photo.id) }
        catch (e: CancellationException) { throw e }
        catch (_: Exception) { false to null }
    }
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp), shadowElevation = 2.dp, modifier = modifier) {
        Column {
            Box(Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFFF0EBF8)),
                contentAlignment = Alignment.Center) {
                val image = thumbnail.second
                when {
                    image != null -> Image(image, contentDescription = "保存した写真",
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    thumbnail.first -> CircularProgressIndicator(Modifier.size(24.dp))
                    else -> Text("表示できません", color = ProfileInk, fontSize = 12.sp)
                }
            }
            Text(remember(photo.timestamp) {
                SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN).format(Date(photo.timestamp))
            }, color = Color(0xFF71809A), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
            TextButton(onClick = onDelete, enabled = !deleting, modifier = Modifier.fillMaxWidth()) {
                Text(if (deleting) "削除中…" else "削除", color = Color(0xFFB54455), fontSize = 12.sp)
            }
        }
    }
}
