package com.ruy.vielasegura.model

/**
 * Níveis de risco de alagamento, definidos pela altura da água (em cm) medida pelos sensores.
 */
enum class NivelAgua(val rotulo: String, val limiteCm: Int) {
    NORMAL("Normal", 0),
    PREPARAR("Preparar", 20),
    ATENCAO("Atenção", 40),
    SAIA_AGORA("Saia agora", 60);

    companion object {
        /** Altura usada como 100% nas barras de nível. */
        const val ALTURA_MAXIMA_CM = 60

        fun deCentimetros(cm: Int): NivelAgua = when {
            cm >= SAIA_AGORA.limiteCm -> SAIA_AGORA
            cm >= ATENCAO.limiteCm -> ATENCAO
            cm >= PREPARAR.limiteCm -> PREPARAR
            else -> NORMAL
        }
    }
}
