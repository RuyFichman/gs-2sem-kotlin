package com.ruy.vielasegura.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ruy.vielasegura.model.NivelAgua

/**
 * Alerta gerado quando a água sobe. O morador responde com "Estou bem" ou "Preciso de ajuda"
 * (alteração de status).
 */
@Entity(tableName = "alertas")
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sensorNome: String,
    val titulo: String,
    val descricao: String,
    val leituraCm: Int,
    val criadoEm: Long,
    /** Momento da resposta do morador ("Estou bem" ou "Preciso de ajuda"). Nulo = aguardando resposta. */
    val confirmadoEm: Long? = null,
    /** true quando a resposta foi "Preciso de ajuda". */
    @ColumnInfo(defaultValue = "0")
    val pediuAjuda: Boolean = false,
)

fun AlertaEntity.nivel(): NivelAgua = NivelAgua.deCentimetros(leituraCm)

/** O morador já respondeu ao alerta (com "Estou bem" ou "Preciso de ajuda"). */
fun AlertaEntity.estaConfirmado(): Boolean = confirmadoEm != null

/** A resposta atual do morador é "Preciso de ajuda". */
fun AlertaEntity.precisaAjuda(): Boolean = confirmadoEm != null && pediuAjuda
