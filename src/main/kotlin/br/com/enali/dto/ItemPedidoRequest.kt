package br.com.enali.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ItemPedidoRequest(
    @field:NotNull(message = "O ID do produto é obrigatório")
    @field:Positive(message = "O ID do produto deve ser positivo")
    val produtoId: Long,

    @field:NotNull(message = "A quantidade é obrigatória")
    @field:Min(value = 1, message = "A quantidade mínima deve ser 1")
    val quantidade: Int
)