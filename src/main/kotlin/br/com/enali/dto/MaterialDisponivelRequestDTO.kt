package br.com.enali.dto

import jakarta.validation.constraints.NotNull

data class MaterialDisponivelRequestDTO(

    @field:NotNull(message = "O campo disponivel é obrigatório")
    val disponivel: Boolean?
)
