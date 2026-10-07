package com.example.ecommerce.dto

import java.time.LocalDate

data class ClienteRequest(

    val nome: String,

    val email: String,

    val senha: String,

    val cpf: String,

    val dataNascimento: LocalDate
)