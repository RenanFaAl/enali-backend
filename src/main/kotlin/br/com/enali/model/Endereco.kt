package br.com.enali.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import java.time.LocalDateTime

@Entity
data class Endereco(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 150)
    val rua: String,

    @Column(nullable = false, length = 20)
    val numero: String,

    @Column(length = 100)
    val complemento: String? = null,

    @Column(nullable = false, length = 100)
    val bairro: String,

    @Column(nullable = false, length = 100)
    val cidade: String,

    @Column(nullable = false, length = 50)
    val estado: String,

    @Column(nullable = false, length = 2)
    val siglaEstado: String,

    @Column(nullable = false, length = 9)
    val cep: String,

    @Column(nullable = false, length = 100)
    val pais: String = "Brasil",

    @Column(length = 150)
    val referencia: String? = null,

    @Column(nullable = false)
    val principal: Boolean = false,

    @Column(nullable = false)
    val ativo: Boolean = true,

    @Column(nullable = false, updatable = false)
    var dataCadastro: LocalDateTime? = null

) {
    @PrePersist
    fun antesDeSalvar() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now()
        }
    }

    constructor() : this(
        id = 0,
        rua = "",
        numero = "",
        complemento = null,
        bairro = "",
        cidade = "",
        estado = "",
        siglaEstado = "",
        cep = "",
        pais = "Brasil",
        referencia = null,
        principal = false,
        ativo = true,
        dataCadastro = null
    )
}