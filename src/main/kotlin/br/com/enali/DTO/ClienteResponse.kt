package com.example.ecommerce.dto

import java.time.LocalDate

data class ClienteResponse(

    val id: Long,

    val nome: String,

    val email: String,

    val cpf: String,

    val dataNascimento: LocalDate,

    val ativo: Boolean
)