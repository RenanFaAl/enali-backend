
package br.com.enali.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class PedidoRequest(

    @field:NotBlank(message = "O número do pedido é obrigatório")
    @field:Size(max = 30, message = "O número deve ter no máximo 30 caracteres")
    val numeroPedido: String,

    @field:Positive(message = "O valor total deve ser maior que zero")
    val valorTotal: Double,

    val dataPedido: LocalDateTime = LocalDateTime.now(),

    @field:NotBlank(message = "O status é obrigatório")
    @field:Size(max = 30, message = "O status deve ter no máximo 30 caracteres")
    val status: String,

    @field:Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
    val observacoes: String? = null
)
