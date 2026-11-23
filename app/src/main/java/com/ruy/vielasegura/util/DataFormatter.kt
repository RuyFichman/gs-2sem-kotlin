package com.ruy.vielasegura.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DataFormatter {

    private val local = Locale.forLanguageTag("pt-BR")

    fun hora(millis: Long): String = SimpleDateFormat("HH:mm", local).format(millis)

    /** "Hoje, 14:32", "Ontem, 19:40" ou "12/11, 08:15". */
    fun diaEHora(millis: Long): String {
        val data = Calendar.getInstance().apply { timeInMillis = millis }
        val hoje = Calendar.getInstance()
        val ontem = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val dia = when {
            mesmoDia(data, hoje) -> "Hoje"
            mesmoDia(data, ontem) -> "Ontem"
            else -> SimpleDateFormat("dd/MM", local).format(millis)
        }
        return "$dia, ${hora(millis)}"
    }

    private fun mesmoDia(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
