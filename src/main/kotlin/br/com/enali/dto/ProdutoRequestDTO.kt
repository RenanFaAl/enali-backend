
package br.com.enali.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal

data class ProdutoRequestDTO(


    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(max = 120, message = "O nome deve ter no máximo 120 caracteres") 
    val nome: String,  
 

    @field:NotBlank(message = "A descrição é obrigatória")
    @field:Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    val descricao: String,

    @field:DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    val preco: BigDecimal,


    val disponivel: Boolean = true,

    @field:DecimalMin(value = "0.0", message = "O desconto não pode ser negativo")
    @field:DecimalMax(value = "100.0", message = "O desconto não pode ultrapassar 100%")
    val desconto: Double = 0.0

)
