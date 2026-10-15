package br.com.enali.dto

import jakarta.validation.constraints.NotNull

data class FornecedorAtivoRequestDTO(

    @field:NotNull(message = "O campo ativo é obrigatório")
    val ativo: Boolean?
)
