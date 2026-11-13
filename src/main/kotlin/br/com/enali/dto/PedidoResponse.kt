
package br.com.enali.dto

import java.time.LocalDateTime

data class PedidoResponse(
    val id: Long,
    val numeroPedido: String,
    val valorTotal: Double,
    val dataPedido: LocalDateTime,
    val status: String,
    val observacoes: String?,
    val clienteId: Long? = null,
    val itens: List<ItemPedidoResponse> = emptyList(),
    val cepEntrega: String? = null,
    val ruaEntrega: String? = null,
    val numeroEntrega: String? = null,
    val bairroEntrega: String? = null,
    val cidadeEntrega: String? = null,
    val estadoEntrega: String? = null
)

data class ItemPedidoResponse(
    val produtoId: Long,
    val produtoNome: String,
    val quantidade: Int,
    val precoUnitario: Double
)
