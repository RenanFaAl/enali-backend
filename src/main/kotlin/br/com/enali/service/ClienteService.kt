
package br.com.enali.service

import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.mapper.ClienteMapper
import br.com.enali.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(
    private val repository: ClienteRepository,
    private val mapper: ClienteMapper
) {

    fun criar(request: ClienteRequest): ClienteResponse {
        val cliente = mapper.toEntity(request)
        return mapper.toResponse(repository.save(cliente))
    }

    fun listarTodos(): List<ClienteResponse> {
        return repository.findAll().map { mapper.toResponse(it) }
    }

    fun buscarPorId(id: Long): ClienteResponse {
        val cliente = repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")
        }
        return mapper.toResponse(cliente)
    }

    fun buscarPorNome(nome: String): List<ClienteResponse> {
        return repository.buscarPorNome(nome).map { mapper.toResponse(it) }
    }

    fun atualizar(id: Long, request: ClienteRequest): ClienteResponse {
        val existente = repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")
        }

        val atualizado = existente.copy(
            nome = request.nome,
            email = request.email,
            senha = request.senha,
            cpf = request.cpf,
            dataNascimento = request.dataNascimento,
            ativo = request.ativo
        )

        return mapper.toResponse(repository.save(atualizado))
    }

    fun remover(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cliente não encontrado"
            )
        }

        repository.deleteById(id)
    }
}
