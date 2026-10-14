package br.com.enali.dto

import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class PedidoRequest(

    @field:Positive(message = "O valor total deve ser maior que zero")
    val valorTotal: Double,

    @field:Size(
        max = 500,
        message = "As observações devem ter no máximo 500 caracteres"
    )
    val observacoes: String? = null
)