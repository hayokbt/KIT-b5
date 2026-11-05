package com.example.kit_b5.data.Room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryPhotoDao {
    @Query("SELECT id, timestamp FROM memory_photos ORDER BY timestamp DESC, id DESC")
    fun observePhotos(): Flow<List<MemoryPhotoInfo>>

    @Query("SELECT * FROM memory_photos WHERE id = :id")
    suspend fun find(id: Long): MemoryPhoto?

    @Insert
    suspend fun insert(photo: MemoryPhoto): Long

    @Query("DELETE FROM memory_photos WHERE id = :id")
    suspend fun delete(id: Long)
}
