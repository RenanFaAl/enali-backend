
package br.com.enali.dto

import java.math.BigDecimal

data class ProdutoResponseDTO(
    val id: Long, 
    val nome: String,
    val descricao: String, 
    val preco: BigDecimal, 
    val disponivel: Boolean,
    val desconto: Double
)
