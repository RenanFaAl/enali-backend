
package br.com.enali.dto

import jakarta.validation.constraints.*

data class EstoqueRequestDTO(

    @field:Positive(message = "O ID do produto deve ser positivo")
    val produtoId: Long,

    @field:Min(value = 0, message = "A quantidade disponível não pode ser negativa")
    val quantidadeDisponivel: Int,

    @field:Min(value = 0, message = "A quantidade reservada não pode ser negativa")
    val quantidadeReservada: Int = 0,

    @field:Min(value = 0, message = "O estoque mínimo não pode ser negativo")
    val estoqueMinimo: Int = 0,

    @field:NotBlank(message = "A localização é obrigatória")
    @field:Size(max = 100, message = "A localização deve ter no máximo 100 caracteres")
    val localizacao: String,

    val ativo: Boolean = true
)
