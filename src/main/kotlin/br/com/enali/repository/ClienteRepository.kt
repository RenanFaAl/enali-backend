
package br.com.enali.repository

import br.com.enali.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ClienteRepository : JpaRepository<Cliente, Long> {

    @Query(
        "SELECT c FROM Cliente c " +
        "WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))"
    )
    fun buscarPorNome(@Param("nome") nome: String): List<Cliente>
}
