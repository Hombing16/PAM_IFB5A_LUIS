package com.example.catatankuliah.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CatatanDao {

    @Query("SELECT * FROM catatan ORDER BY id DESC")
    fun semuaCatatan(): Flow<List<Catatan>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun tambah(catatan: Catatan)

    @Update
    suspend fun ubah(catatan: Catatan)

    @Delete
    suspend fun hapus(catatan: Catatan)
}
