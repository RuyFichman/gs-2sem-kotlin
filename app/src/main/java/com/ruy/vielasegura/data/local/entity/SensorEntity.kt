package com.ruy.vielasegura.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Sensor de nível de água instalado na viela (leituras simuladas no protótipo). */
@Entity(tableName = "sensores")
data class SensorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    /** Trecho usado em frases, ex.: "no bueiro da entrada". */
    val local: String,
    val viela: String,
    val nivelCm: Int,
    val ultimaLeitura: Long,
)
