package br.com.enali.dto

import java.time.LocalDateTime

data class EnderecoResponseDTO(
    val id: Long,
    val rua: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val siglaEstado: String,
    val cep: String,
    val pais: String,
    val referencia: String?,
    val principal: Boolean,
    val ativo: Boolean,
    val dataCadastro: LocalDateTime?
)
