package br.com.enali.DTO
import java.math.BigDecimal

data class PedidoRequest(

    val numeroPedido: String,
    val valorTotal: BigDecimal,
    val status: String,
    val observacoes: String?
)