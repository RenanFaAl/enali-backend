package br.com.enali.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AdministradorAtualizacaoRequestDTO(

    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(
        min = 2,
        max = 150,
        message = "O nome deve ter entre 2 e 150 caracteres"
    )
    val nome: String,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "Informe um e-mail válido")
    @field:Size(
        max = 254,
        message = "O e-mail deve ter no máximo 254 caracteres"
    )
    val email: String
)
