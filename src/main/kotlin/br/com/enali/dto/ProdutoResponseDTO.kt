package br.com.enali.dto

import java.math.BigDecimal

data class ProdutoResponseDTO(
    val id: Long,
    val nome: String,
    val descricao: String,
    val preco: BigDecimal,
    val disponivel: Boolean,
    val desconto: Double,
    val imagensUrls: List<String> = emptyList()
)

data class ProdutoImagemResponseDTO(
    val id: Long,
    val produtoId: Long,
    val url: String,
    val tipoMime: String,
    val ordem: Int,
    val tamanhoBytes: Int
)