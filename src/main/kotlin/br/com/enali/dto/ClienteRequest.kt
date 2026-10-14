package br.com.enali.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class ClienteRequest(

    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    val nome: String,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "Informe um e-mail válido")
    @field:Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
    val email: String,

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
    val senha: String,

    @field:NotBlank(message = "O CPF é obrigatório")
    @field:Pattern(
        regexp = "^\\d{11}$",
        message = "O CPF deve conter 11 números, sem pontos ou traços"
    )
    val cpf: String,

    @field:NotNull(message = "A data de nascimento é obrigatória")
    val dataNascimento: LocalDate
)
