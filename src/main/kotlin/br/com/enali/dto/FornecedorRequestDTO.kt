package br.com.enali.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class FornecedorRequestDTO(

    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    val nome: String,

    @field:NotBlank(message = "O CNPJ é obrigatório")
    @field:Pattern(
        regexp = """^\d{2}\.?\d{3}\.?\d{3}/?\d{4}-?\d{2}$""",
        message = "Informe um CNPJ válido, como 12.345.678/0001-90"
    )
    val cnpj: String,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "Informe um e-mail válido")
    @field:Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
    val email: String,

    @field:NotBlank(message = "O telefone é obrigatório")
    @field:Pattern(
        regexp = """^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$""",
        message = "Informe um telefone válido, como (11) 91234-5678"
    )
    val telefone: String
)
