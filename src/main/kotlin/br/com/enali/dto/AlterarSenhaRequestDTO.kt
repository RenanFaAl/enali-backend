package br.com.enali.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AlterarSenhaRequestDTO(

    @field:NotBlank(message = "A senha atual é obrigatória")
    val senhaAtual: String,

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 8, max = 254, message = "A senha deve ter entre 8 e 254 caracteres")
    @field:Pattern(
        regexp = """^(?=\S*[A-Z])(?=\S*[a-z])(?=\S*\d)(?=\S*[^A-Za-z0-9\s])\S+$""",
        message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial, sem espaços"
    )
    val senhaNova: String,

    @field:NotBlank(message = "A confirmação da senha é obrigatória")
    val confirmarSenha: String

)
