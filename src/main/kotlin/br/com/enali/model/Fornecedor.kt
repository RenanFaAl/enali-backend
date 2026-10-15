package br.com.enali.model

import jakarta.persistence.*

@Entity
@Table(name = "fornecedores")
data class Fornecedor(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(nullable = false, length = 150)
    val nome: String,

    @field:Column(nullable = false, unique = true, length = 14)
    val cnpj: String,

    @field:Column(nullable = false, length = 254)
    val email: String,

    @field:Column(nullable = false, length = 11)
    val telefone: String,

    @field:Column(nullable = false)
    val ativo: Boolean = true
)
