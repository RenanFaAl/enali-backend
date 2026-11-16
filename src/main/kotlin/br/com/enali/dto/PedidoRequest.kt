package br.com.enali.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class PedidoRequest(
    @field:NotNull(message = "O ID do endereço de entrega é obrigatório")
    @field:Positive(message = "O ID do endereço deve ser positivo")
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