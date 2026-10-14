
package br.com.enali.service

import br.com.enali.dto.ProdutoRequestDTO
import br.com.enali.dto.ProdutoResponseDTO
import br.com.enali.mapper.ProdutoMapper
import br.com.enali.repository.ProdutoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProdutoService(
    private val repository: ProdutoRepository,
    private val mapper: ProdutoMapper
) {

    fun criar(request: ProdutoRequestDTO): ProdutoResponseDTO {
        val produto = mapper.toEntity(request)
        return mapper.toResponse(repository.save(produto))
    }

    fun listarTodos(): List<ProdutoResponseDTO> {
        return repository.findAll().map {
            mapper.toResponse(it)
        }
    }

    fun buscarPorId(id: Long): ProdutoResponseDTO {
        val produto = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Produto não encontrado."
            )
        }

        return mapper.toResponse(produto)
    }

    fun buscarPorNome(nome: String): List<ProdutoResponseDTO> {
        return repository.buscarPorNome(nome.trim()).map {
            mapper.toResponse(it)
        }
    }

    fun atualizar(
        id: Long,
        request: ProdutoRequestDTO
    ): ProdutoResponseDTO {
        val existente = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Produto não encontrado."
            )
        }

        val atualizado = existente.copy(
            nome = request.nome.trim(),
            descricao = request.descricao.trim(),
            preco = request.preco,
            disponivel = request.disponivel,
            desconto = request.desconto
        )

        return mapper.toResponse(repository.save(atualizado))
    }

    fun remover(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Produto não encontrado."
            )
        }

        repository.deleteById(id)
    }
}
