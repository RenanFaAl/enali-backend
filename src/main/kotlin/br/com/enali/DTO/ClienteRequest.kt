package br.com.enali.DTO

import java.time.LocalDate

data class ClienteRequest(

    val nome: String,

    val email: String,

    val senha: String,

    val cpf: String,

    val dataNascimento: LocalDate
)