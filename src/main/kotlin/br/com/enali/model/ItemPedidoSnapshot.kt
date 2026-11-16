package br.com.enali.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
data class ItemPedidoSnapshot(
    @Column(name = "produto_id_original", nullable = false)
    val produtoId: Long,

    @Column(name = "produto_nome", nullable = false, length = 120)
    val nome: String,

    @Column(name = "produto_descricao", nullable = false, length = 500)
    val descricao: String,

    @Column(name = "produto_preco", nullable = false, precision = 12, scale = 2)
    val precoUnitario: BigDecimal,

    @Column(name = "produto_desconto", nullable = false)
    val desconto: Double,

    @Column(name = "quantidade", nullable = false)
    val quantidade: Int,

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    val subtotal: BigDecimal
)