package br.com.enali.config

import java.security.Principal

data class UsuarioAutenticado(
    val id: Long,
    val email: String,
    val tipoUsuario: String,
    val nivelAcesso: String? = null
) : Principal {

    override fun getName(): String = email
}
