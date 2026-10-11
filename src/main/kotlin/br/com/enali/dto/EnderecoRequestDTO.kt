package br.com.enali.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class EnderecoRequestDTO(

    @field:NotBlank(message = "A rua é obrigatória")
    @field:Size(max = 150, message = "A rua deve ter no máximo 150 caracteres")
    val rua: String,

    @field:NotBlank(message = "O número é obrigatório")
    @field:Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
    val numero: String,

    @field:Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    val complemento: String? = null,

    @field:NotBlank(message = "O bairro é obrigatório")
    @field:Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
    val bairro: String,

    @field:NotBlank(message = "A cidade é obrigatória")
    @field:Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    val cidade: String,

    @field:NotBlank(message = "O estado é obrigatório")
    @field:Size(max = 50, message = "O estado deve ter no máximo 50 caracteres")
    val estado: String,

    @field:NotBlank(message = "A sigla do estado é obrigatória")
    @field:Pattern(
        regexp = "^(AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO)$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "Informe uma sigla de estado válida"
    )
    val siglaEstado: String,

    @field:NotBlank(message = "O CEP é obrigatório")
    @field:Pattern(
        regexp = """^\d{5}-?\d{3}$""",
        message = "Informe um CEP válido, como 01001-000"
    )
    val cep: String,

    @field:NotBlank(message = "O país é obrigatório")
    @field:Size(max = 100, message = "O país deve ter no máximo 100 caracteres")
    val pais: String = "Brasil",

    @field:Size(max = 150, message = "A referência deve ter no máximo 150 caracteres")
    val referencia: String? = null,

    val principal: Boolean = false
)
