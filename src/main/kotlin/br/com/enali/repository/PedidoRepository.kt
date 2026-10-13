
package br.com.enali.repository

import br.com.enali.model.Pedido
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PedidoRepository : JpaRepository<Pedido, Long> {

    @Query(
        "SELECT p FROM Pedido p " +
        "WHERE LOWER(p.numeroPedido) LIKE LOWER(CONCAT('%', :numero, '%'))"
    )
    fun buscarPorNumero(@Param("numero") numero: String): List<Pedido>
}
