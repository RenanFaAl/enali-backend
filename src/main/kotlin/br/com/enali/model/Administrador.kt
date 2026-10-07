package br.com.enali.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Column

@Entity
data class Administrador(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nome: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val senha: String,

    @Column(nullable = false)
    val ativo: Boolean,

    @Column(nullable = false)
    val nivelAcesso: String
) {
    constructor() : this(0, "", "", "", true, "ADMIN_COMUM")
}