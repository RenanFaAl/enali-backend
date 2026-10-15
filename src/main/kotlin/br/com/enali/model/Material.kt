package br.com.enali.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "materiais")
data class Material(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(nullable = false, length = 150)
    val nome: String,

    @field:Column(length = 500)
    val descricao: String? = null,

    @field:Column(nullable = false)
    val quantidade: Int = 0,

    @field:Column(nullable = false, precision = 10, scale = 2)
    val custo: BigDecimal,

    @field:Column(nullable = false)
    val disponivel: Boolean = true
)
