
package br.com.enali.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "clientes")
data class Cliente(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(nullable = false, length = 100)
    val nome: String,

    @field:Column(nullable = false, unique = true, length = 150)
    val email: String,

    @field:Column(nullable = false, length = 100)
    val senha: String,

    @field:Column(nullable = false, unique = true, length = 11)
    val cpf: String,

    @field:Column(name = "data_nascimento", nullable = false)
    val dataNascimento: LocalDate,

    @field:Column(nullable = false)
    val ativo: Boolean = true
)
