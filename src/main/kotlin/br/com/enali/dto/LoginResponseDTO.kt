package br.com.enali.dto

data class LoginResponseDTO(
    val token: String,
    val tipo: String = "Bearer",
    val id: Long,
    val nome: String,
    val email: String,
    val nivelAcesso: String
)
