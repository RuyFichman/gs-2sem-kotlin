package com.ruy.vielasegura.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ruy.vielasegura.data.local.entity.AlertaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Query("SELECT * FROM alertas ORDER BY criadoEm DESC")
    fun observarTodos(): Flow<List<AlertaEntity>>

    @Query("SELECT * FROM alertas WHERE id = :id")
    fun observarPorId(id: Long): Flow<AlertaEntity?>

    @Insert
    suspend fun inserir(alerta: AlertaEntity): Long

    @Insert
    suspend fun inserirTodos(alertas: List<AlertaEntity>)

    /** Registra a resposta do morador: "Estou bem" (pediuAjuda = false) ou "Preciso de ajuda" (true). */
    @Query("UPDATE alertas SET confirmadoEm = :quando, pediuAjuda = :pediuAjuda WHERE id = :id")
    suspend fun responder(id: Long, quando: Long, pediuAjuda: Boolean)
}
