package com.ruy.vielasegura.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ruy.vielasegura.data.local.entity.SensorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {

    @Query("SELECT * FROM sensores ORDER BY id")
    fun observarTodos(): Flow<List<SensorEntity>>

    @Query("SELECT * FROM sensores ORDER BY id")
    suspend fun listar(): List<SensorEntity>

    @Query("SELECT COUNT(*) FROM sensores")
    suspend fun contar(): Int

    @Insert
    suspend fun inserirTodos(sensores: List<SensorEntity>)

    @Update
    suspend fun atualizar(sensor: SensorEntity)
}
