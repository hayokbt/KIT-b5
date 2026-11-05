package com.example.kit_b5.data.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_photos")
data class MemoryPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val image: ByteArray,
    val timestamp: Long
)

/** 一覧取得時には画像本体を読み込まない。 */
data class MemoryPhotoInfo(val id: Long, val timestamp: Long)
