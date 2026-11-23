package com.ruy.vielasegura.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ruy.vielasegura.data.local.entity.CasaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CasaDao {

    @Query("SELECT * FROM casas ORDER BY id DESC")
    fun observarTodas(): Flow<List<CasaEntity>>

    @Query("SELECT * FROM casas WHERE id = :id")
    suspend fun buscarPorId(id: Long): CasaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(casa: CasaEntity): Long

    @Update
    suspend fun atualizar(casa: CasaEntity)

    @Delete
    suspend fun excluir(casa: CasaEntity)
}
