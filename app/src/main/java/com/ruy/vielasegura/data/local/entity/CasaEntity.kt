package com.ruy.vielasegura.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Casa cadastrada pelo morador. É o registro principal criado, editado e excluído pelo usuário.
 */
@Entity(tableName = "casas")
data class CasaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val apelido: String,
    val viela: String,
    val referencia: String,
    val moradores: Int,
    /** Código gerado ao salvar (ex.: VC-012). */
    val codigo: String = "",
)
