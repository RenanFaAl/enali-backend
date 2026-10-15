
package br.com.enali.dto

import java.time.LocalDateTime

data class EstoqueResponseDTO(
    val id: Long,
    val produtoId: Long,
    val produtoNome: String,
    val quantidadeDisponivel: Int,
    val quantidadeReservada: Int,
    val estoqueMinimo: Int,
    val localizacao: String,
    val ativo: Boolean,
    val dataAtualizacao: LocalDateTime
)
