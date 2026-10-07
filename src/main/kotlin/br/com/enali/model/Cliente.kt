package br.com.enali.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "clientes")
data class Cliente(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nome: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val senha: String,

    @Column(nullable = false, unique = true)
    val cpf: String,

    @Column(nullable = false)
    val dataNascimento: LocalDate,

    @Column(nullable = false)
    val ativo: Boolean = true
)