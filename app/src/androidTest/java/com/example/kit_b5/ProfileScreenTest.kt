package com.example.kit_b5

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kit_b5.ui.ProfileScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun profileShowsAlbumAndOpensWishTabs() {
        compose.setContent { MaterialTheme { ProfileScreen(nickname = "テスト") } }
        compose.onNodeWithText("わたしのこと").assertIsDisplayed()
        compose.onNodeWithText("希望を書いてみる", substring = true).assertIsEnabled()
        compose.onNodeWithText("＋ 写真を追加").performScrollTo().assertIsDisplayed().assertIsEnabled()
        compose.onNodeWithText("保存した写真").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("希望を書いてみる", substring = true).performScrollTo().performClick()
        compose.onNodeWithText("希望を整理する").assertIsDisplayed()
        compose.onNodeWithText("価値観分析").performClick()
        compose.onNodeWithText("正解はありません。今の気持ちに近いほうを選んでください。回答はこの端末に保存されます。").assertIsDisplayed()
        compose.waitUntil(10000) { compose.onAllNodesWithTag("value_1_B").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("value_1_B").performClick()
        compose.waitUntil(10000) {
            runCatching { compose.onNodeWithTag("value_1_B").assertIsSelected(); true }.getOrDefault(false)
        }
        compose.onNodeWithTag("value_1_A").performClick()
        compose.waitUntil(10000) {
            runCatching { compose.onNodeWithTag("value_1_A").assertIsSelected(); true }.getOrDefault(false)
        }
        compose.onNodeWithText("閉じる").performClick()
        compose.onNodeWithText("希望を書いてみる", substring = true).performScrollTo().performClick()
        compose.onNodeWithText("価値観分析").performClick()
        compose.waitUntil(10000) {
            runCatching { compose.onNodeWithTag("value_1_A").assertIsSelected(); true }.getOrDefault(false)
        }
    }
}
