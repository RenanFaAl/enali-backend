
package br.com.enali.model

import jakarta.persistence.*
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
    val observacoes: String? = null
)
