package br.com.enali.service

import br.com.enali.dto.LoginRequestDTO
import br.com.enali.dto.LoginResponseDTO
import br.com.enali.repository.AdministradorRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthAdministradorService(
    private val repository: AdministradorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    fun login(request: LoginRequestDTO): LoginResponseDTO {
        val email = request.email.trim().lowercase()

        val administrador = repository.findByEmail(email)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
            )

        if (!passwordEncoder.matches(request.senha, administrador.senha)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
            )
        }

        if (!administrador.ativo) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
            )
        }

        val token = jwtService.gerarToken(administrador)

        return LoginResponseDTO(
            token = token,
            id = administrador.id,
            nome = administrador.nome,
            email = administrador.email,
            nivelAcesso = administrador.nivelAcesso
        )
    }
}
