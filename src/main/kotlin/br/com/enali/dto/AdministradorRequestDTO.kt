package br.com.enali.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AdministradorRequestDTO(

    @field:NotBlank
    @field:Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    val nome: String,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "Informe um e-mail válido")
    @field:Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
    val email: String,

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 8, max = 2544, message = "A senha deve ter entre 8 e 254 caracteres")
    @field:Pattern(
        regexp = """^(?=\S*[A-Z])(?=\S*[a-z])(?=\S*\d)(?=\S*[^A-Za-z0-9\s])\S+$""",
        message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial, sem espaços"
    )
    val senha: String
)
