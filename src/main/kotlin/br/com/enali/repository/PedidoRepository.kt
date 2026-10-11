package com.example.ecommerce.repository

import com.example.ecommerce.model.Pedido
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PedidoRepository : JpaRepository<Pedido, Long> {

    fun findByNumeroPedido(numeroPedido: String): Pedido?

    fun findByStatus(status: String): List<Pedido>
}