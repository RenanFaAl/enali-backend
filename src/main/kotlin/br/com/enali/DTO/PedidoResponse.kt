
package br.com.enali.DTO

data class PedidoResponse(
    val id: Long,
    val numeroPedido: String,
    val valorTotal: Double,
    val status: String,
    val observacoes: String
)
