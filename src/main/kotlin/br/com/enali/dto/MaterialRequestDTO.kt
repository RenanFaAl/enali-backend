package br.com.enali.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class MaterialRequestDTO(

    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    val nome: String,

    @field:Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    val descricao: String? = null,

    @field:NotNull(message = "A quantidade é obrigatória")
    @field:PositiveOrZero(message = "A quantidade não pode ser negativa")
    val quantidade: Int?,

    @field:NotNull(message = "O custo é obrigatório")
    @field:DecimalMin(value = "0.01", message = "O custo deve ser maior que zero")
    @field:Digits(integer = 8, fraction = 2, message = "O custo deve ter no máximo 8 dígitos inteiros e 2 casas decimais")
    val custo: BigDecimal?,

    val disponivel: Boolean = true
)
