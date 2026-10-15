package br.com.enali.dto

import java.math.BigDecimal

data class MaterialResponseDTO(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val quantidade: Int,
    val custo: BigDecimal,
    val disponivel: Boolean
)
