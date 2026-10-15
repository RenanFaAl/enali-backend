package br.com.enali.dto

data class FornecedorResponseDTO(
    val id: Long,
    val nome: String,
    val cnpj: String,
    val email: String,
    val telefone: String,
    val ativo: Boolean
)
