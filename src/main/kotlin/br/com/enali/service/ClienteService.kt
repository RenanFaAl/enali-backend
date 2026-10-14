package br.com.enali.service

import br.com.enali.dto.ClienteAtualizacaoRequest
import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.mapper.ClienteMapper
import br.com.enali.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper,
    private val passwordEncoder: PasswordEncoder
) {


    fun buscarMeusDados(emailAutenticado: String): ClienteResponse {
        val email = emailAutenticado.trim().lowercase()

        val cliente = repository.findByEmail(email)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            )

        return mapper.toResponse(cliente)
    }

    fun atualizarMeusDados(
        emailAutenticado: String,
        request: ClienteAtualizacaoRequest
    ): ClienteResponse {
        val emailAtual = emailAutenticado.trim().lowercase()

        val existente = repository.findByEmail(emailAtual)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            )

        val novoEmail = request.email.trim().lowercase()
        val cpf = request.cpf.trim()

        val clienteComMesmoEmail = repository.findByEmail(novoEmail)

        if (clienteComMesmoEmail != null &&
            clienteComMesmoEmail.id != existente.id
        ) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este e-mail já está cadastrado."
            )
        }

        val clienteComMesmoCpf = repository.findByCpf(cpf)

        if (clienteComMesmoCpf != null &&
            clienteComMesmoCpf.id != existente.id
        ) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este CPF já está cadastrado."
            )
        }

        val atualizado = existente.copy(
            nome = request.nome.trim(),
            email = novoEmail,
            cpf = cpf,
            dataNascimento = request.dataNascimento
        )

        return mapper.toResponse(repository.save(atualizado))
    }

    fun alterarMinhaSenha(
        emailAutenticado: String,
        senhaAtual: String,
        senhaNova: String
    ) {
        val email = emailAutenticado.trim().lowercase()

        val cliente = repository.findByEmail(email)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            )

        if (!passwordEncoder.matches(senhaAtual, cliente.senha)) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A senha atual está incorreta."
            )
        }

        val novaSenhaHash = requireNotNull(
            passwordEncoder.encode(senhaNova)
        )

        repository.save(
            cliente.copy(senha = novaSenhaHash)
        )
    }


    fun listarTodos(): List<ClienteResponse> {
        return repository.findAll().map { mapper.toResponse(it) }
    }

    fun buscarPorId(id: Long): ClienteResponse {
        val cliente = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            )
        }

        return mapper.toResponse(cliente)
    }

    fun buscarPorNome(nome: String): List<ClienteResponse> {
        return repository.buscarPorNome(nome.trim())
            .map { mapper.toResponse(it) }
    }

    fun alterarAtivo(id: Long, ativo: Boolean): ClienteResponse {
        val cliente = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            )
        }

        val atualizado = cliente.copy(ativo = ativo)

        return mapper.toResponse(repository.save(atualizado))
    }

    fun remover(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado."
            )
        }

        repository.deleteById(id)
    }
}
