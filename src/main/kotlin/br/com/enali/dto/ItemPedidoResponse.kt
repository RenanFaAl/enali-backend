package br.com.enali.dto

import java.math.BigDecimal

data class ItemPedidoResponse(
    val produtoId: Long,
    val nome: String,
    val descricao: String,
    val precoUnitario: BigDecimal,
    val desconto: Double,
    val quantidade: Int,
    val subtotal: BigDecimal
)