package br.com.enali.repository

import br.com.enali.model.PedidoImagem
import org.springframework.data.jpa.repository.JpaRepository

interface PedidoImagemRepository : JpaRepository<PedidoImagem, Long> {

    fun findAllByPedidoIdOrderByIdAsc(pedidoId: Long): List<PedidoImagem>

    fun countByPedidoId(pedidoId: Long): Long

    fun findByIdAndPedidoId(id: Long, pedidoId: Long): PedidoImagem?
}
