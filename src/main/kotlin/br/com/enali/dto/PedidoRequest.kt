package br.com.enali.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class PedidoRequest(

    val enderecoId: Long,

    @field:NotEmpty(message = "O pedido deve conter pelo menos um item")
    @field:Valid
    val itens: List<ItemPedidoRequest>,

    @field:Size(
        max = 500,
        message = "As observações devem ter no máximo 500 caracteres"
    )
    val observacoes: String? = null
)

data class ItemPedidoRequest(

    val produtoId: Long,

    @field:Min(value = 1, message = "A quantidade deve ser maior que zero")
    val quantidade: Int
)
