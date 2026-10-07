package com.example.ecommerce.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "pedidos")
data class Pedido(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val numeroPedido: String,

    @Column(nullable = false)
    val valorTotal: BigDecimal,

    @Column(nullable = false)
    val dataPedido: LocalDateTime,

    @Column(nullable = false)
    val status: String,

    @Column
    val observacoes: String? = null
)