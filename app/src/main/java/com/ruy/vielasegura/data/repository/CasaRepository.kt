package com.ruy.vielasegura.data.repository

import com.ruy.vielasegura.data.local.dao.CasaDao
import com.ruy.vielasegura.data.local.entity.CasaEntity
import com.ruy.vielasegura.model.Vielas
import kotlinx.coroutines.flow.Flow

class CasaRepository(private val casaDao: CasaDao) {

    fun observarCasas(): Flow<List<CasaEntity>> = casaDao.observarTodas()

    suspend fun buscar(id: Long): CasaEntity? = casaDao.buscarPorId(id)

    /**
     * Cria ou atualiza a casa e devolve o código gerado (ex.: VC-012).
     * O código depende do id, por isso é gravado logo após a inserção.
     */
    suspend fun salvar(casa: CasaEntity): String {
        val id = if (casa.id == 0L) casaDao.inserir(casa) else casa.id
        val codigo = Vielas.codigoCasa(casa.viela, id)
        casaDao.atualizar(casa.copy(id = id, codigo = codigo))
        return codigo
    }

    suspend fun excluir(casa: CasaEntity) = casaDao.excluir(casa)

    /** Usado pelo "Desfazer" após excluir: reinsere a casa com o mesmo id e código. */
    suspend fun restaurar(casa: CasaEntity) {
        casaDao.inserir(casa)
    }
}
