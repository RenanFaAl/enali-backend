package br.com.enali.service

import br.com.enali.dto.LoginRequestDTO
import br.com.enali.dto.LoginResponseDTO
import br.com.enali.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthClienteService(
    private val repository: ClienteRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    fun login(request: LoginRequestDTO): LoginResponseDTO {
        val email = request.email.trim().lowercase()

        val cliente = repository.findByEmail(email)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
            )

        if (!passwordEncoder.matches(request.senha, cliente.senha)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
            )
        }

        if (!cliente.ativo) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos."
            )
        }

        val token = jwtService.gerarToken(cliente)

        return LoginResponseDTO(
            token = token,
            id = cliente.id,
            nome = cliente.nome,
            email = cliente.email,
            nivelAcesso = cliente.nivelAcesso
        )
    }
}
