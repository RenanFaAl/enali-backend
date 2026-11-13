package br.com.enali.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
@Table(name = "itens_pedido")
data class ItemPedido(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "produto_id", nullable = false)
    val produto: Produto,

    @field:Column(nullable = false)
    val quantidade: Int,

    // Preço congelado no momento da compra (RN27), já com o desconto do produto
    @field:Column(name = "preco_unitario", nullable = false)
    val precoUnitario: Double
) {

    fun calcularSubtotal(): BigDecimal {
        return BigDecimal.valueOf(precoUnitario).multiply(BigDecimal(quantidade))
    }

    companion object {

        fun de(produto: Produto, quantidade: Int): ItemPedido {
            // desconto é percentual (0 a 100), conforme ProdutoRequestDTO
            val fator = BigDecimal.valueOf(100 - produto.desconto)
                .divide(BigDecimal(100))
            val precoUnitario = produto.preco.multiply(fator)
                .setScale(2, RoundingMode.HALF_UP)

            return ItemPedido(
                produto = produto,
                quantidade = quantidade,
                precoUnitario = precoUnitario.toDouble()
            )
        }
    }
}
