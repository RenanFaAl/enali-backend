
package br.com.enali.dto

import java.time.LocalDateTime

data class PedidoResponse(
    val id: Long,
    val numeroPedido: String,
    val valorTotal: Double,
    val dataPedido: LocalDateTime,
    val status: String,
    val observacoes: String?
)
