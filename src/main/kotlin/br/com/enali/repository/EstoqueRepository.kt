
package br.com.enali.repository

import br.com.enali.model.Estoque
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EstoqueRepository : JpaRepository<Estoque, Long> {

    // Trava a linha até o fim da transação: dois pedidos simultâneos não vendem o mesmo saldo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findFirstByProdutoIdOrderByIdAsc(produtoId: Long): Estoque?

    @Query(
        "SELECT e FROM Estoque e " +
        "WHERE LOWER(e.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%'))"
    )
    fun buscarPorLocalizacao(
        @Param("localizacao") localizacao: String
    ): List<Estoque>
}
