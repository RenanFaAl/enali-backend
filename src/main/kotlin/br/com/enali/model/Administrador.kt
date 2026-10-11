package br.com.enali.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.PrePersist
import java.time.LocalDateTime

@Entity
data class Administrador(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 150)
    val nome: String,

    @Column(nullable = false, unique = true, length = 254)
    val email: String,

    @Column(nullable = false, length = 254)
    val senha: String,

    @Column(nullable = false)
    val ativo: Boolean,

    @Column(nullable = false, length = 30)
    val nivelAcesso: String,

    @Column(nullable = false, updatable = false)
    var dataCriacao: LocalDateTime? = null

) {

    @PrePersist
    fun antesDeSalvar() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now()
        }
    }

    constructor() : this(0, "", "", "", true, "ADMIN_COMUM", dataCriacao = null)
}