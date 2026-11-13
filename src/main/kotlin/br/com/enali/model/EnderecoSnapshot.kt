package br.com.enali.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class EnderecoSnapshot(
    @Column(name = "endereco_rua", nullable = false, length = 150)
    val rua: String,

    @Column(name = "endereco_numero", nullable = false, length = 20)
    val numero: String,

    @Column(name = "endereco_complemento", length = 100)
    val complemento: String? = null,

    @Column(name = "endereco_bairro", nullable = false, length = 100)
    val bairro: String,

    @Column(name = "endereco_cidade", nullable = false, length = 100)
    val cidade: String,

    @Column(name = "endereco_estado", nullable = false, length = 50)
    val estado: String,

    @Column(name = "endereco_sigla_estado", nullable = false, length = 2)
    val siglaEstado: String,

    @Column(name = "endereco_cep", nullable = false, length = 9)
    val cep: String,

    @Column(name = "endereco_pais", nullable = false, length = 100)
    val pais: String = "Brasil",

    @Column(name = "endereco_referencia", length = 150)
    val referencia: String? = null
)