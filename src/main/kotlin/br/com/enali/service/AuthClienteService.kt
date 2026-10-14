package br.com.enali.service

import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.dto.LoginRequestDTO
import br.com.enali.dto.LoginResponseDTO
import br.com.enali.mapper.ClienteMapper
import br.com.enali.model.Cliente
import br.com.enali.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthClienteService(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun cadastrar(request: ClienteRequest): ClienteResponse {
        val email = request.email.trim().lowercase()
        val cpf = request.cpf.trim()

        if (repository.existsByEmail(email)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este e-mail já está cadastrado."
            )
        }

        if (repository.existsByCpf(cpf)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este CPF já está cadastrado."
            )
        }

        val cliente = mapper.toEntity(request).copy(
            email = email,
            cpf = cpf,
            senha = requireNotNull(passwordEncoder.encode(request.senha)),
            ativo = true
        )

        return mapper.toResponse(repository.save(cliente))
    }

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
            nivelAcesso = null,
            tipoUsuario = "CLIENTE"
        )
    }
}