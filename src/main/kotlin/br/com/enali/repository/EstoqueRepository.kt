
package br.com.enali.repository

import br.com.enali.model.Estoque
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EstoqueRepository : JpaRepository<Estoque, Long> {

    @Query(
        "SELECT e FROM Estoque e " +
        "WHERE LOWER(e.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%'))"
    )
    fun buscarPorLocalizacao(
        @Param("localizacao") localizacao: String
    ): List<Estoque>
}
