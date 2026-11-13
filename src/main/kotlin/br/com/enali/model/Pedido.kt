
package br.com.enali.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Entity
@Table(name = "pedidos")
data class Pedido(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(name = "numero_pedido", nullable = false, length = 30)
    val numeroPedido: String,

    @field:Column(name = "valor_total", nullable = false)
    val valorTotal: Double,

    @field:Column(name = "data_pedido", nullable = false)
    val dataPedido: LocalDateTime = LocalDateTime.now(),

    @field:Column(nullable = false, length = 30)
    val status: String,

    @field:Column(length = 500)
    val observacoes: String? = null,

    // Colunas abaixo são anuláveis para não quebrar pedidos já gravados antes do UC05
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "cliente_id")
    val cliente: Cliente? = null,

    @field:OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @field:JoinColumn(name = "pedido_id")
    val itens: List<ItemPedido> = emptyList(),

    // RN28: cópia do endereço de entrega; o histórico não muda se o cliente alterar o endereço
    @field:Column(name = "cep_entrega", length = 9)
    val cepEntrega: String? = null,

    @field:Column(name = "rua_entrega", length = 150)
    val ruaEntrega: String? = null,

    @field:Column(name = "numero_entrega", length = 20)
    val numeroEntrega: String? = null,

    @field:Column(name = "bairro_entrega", length = 100)
    val bairroEntrega: String? = null,

    @field:Column(name = "cidade_entrega", length = 100)
    val cidadeEntrega: String? = null,

    @field:Column(name = "estado_entrega", length = 2)
    val estadoEntrega: String? = null
) {

    fun calcularTotal(): Double {
        return itens
            .fold(BigDecimal.ZERO) { total, item -> total.add(item.calcularSubtotal()) }
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }
}
