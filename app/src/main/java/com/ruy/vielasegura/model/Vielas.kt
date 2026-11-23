package com.ruy.vielasegura.model

/** Vielas atendidas pelo protótipo (dados simulados). */
object Vielas {
    const val PADRAO = "Viela do Cruzeiro"

    val todas = listOf(
        "Viela do Cruzeiro",
        "Viela da Paz",
        "Viela São Jorge",
        "Viela Boa Esperança",
    )

    private val conectivos = setOf("de", "da", "do", "das", "dos", "e")

    /** "Viela do Cruzeiro" -> "VC"; "Viela São Jorge" -> "VS". */
    fun sigla(viela: String): String =
        viela.split(" ")
            .filter { it.isNotBlank() && it.lowercase() !in conectivos }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "CS" }

    /** Gera o código da casa, por exemplo "VC-012". */
    fun codigoCasa(viela: String, id: Long): String = "%s-%03d".format(sigla(viela), id)
}
