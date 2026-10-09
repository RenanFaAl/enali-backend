package br.com.enali.dto

import java.time.LocalDateTime

data class AdministradorResponseDTO(
    val id: Long,
    val nome: String,
    val email: String,
    val ativo: Boolean,
    val nivelAcesso: String,
    val dataCriacao: LocalDateTime?
) {}
