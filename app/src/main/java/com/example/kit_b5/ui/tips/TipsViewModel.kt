package com.example.kit_b5.ui.tips

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.kit_b5.data.tips.TipCatalog

class TipsViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    val tips = TipCatalog.tips
    val sources = TipCatalog.sources
    val expandedIds = savedState.getStateFlow("expanded_tip_ids", intArrayOf())

    fun toggle(id: Int) {
        val current = expandedIds.value.toSet()
        savedState["expanded_tip_ids"] = if (id in current) (current - id).toIntArray()
            else (current + id).toIntArray()
    }
}
