package br.com.enali.dto

data class AdministradorResponseDTO(
    val id: Long,
    val nome: String,
    val email: String,
    val ativo: Boolean,
    val nivelAcesso: String
) {}
