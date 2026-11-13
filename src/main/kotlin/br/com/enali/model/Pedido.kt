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
    val observacoes: String? = null,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "cliente_id", nullable = true)
    val cliente: Cliente? = null,
    
    @field:Column(name = "cliente_id_snapshot", nullable = false)
    val clienteId: Long,

    @field:Column(name = "cliente_nome", nullable = false, length = 100)
    val clienteNome: String,

    @field:Column(name = "cliente_email", nullable = false, length = 150)
    val clienteEmail: String,

    @field:Column(name = "cliente_cpf", nullable = false, length = 11)
    val clienteCpf: String,

    @field:Embedded
    val enderecoEntrega: EnderecoSnapshot,

    @field:ElementCollection
    @field:CollectionTable(name = "pedido_itens", joinColumns = [JoinColumn(name = "pedido_id")])
    val itens: List<ItemPedidoSnapshot> = listOf()
)