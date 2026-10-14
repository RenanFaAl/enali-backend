
package br.com.enali.service

import br.com.enali.dto.EstoqueRequestDTO
import br.com.enali.dto.EstoqueResponseDTO
import br.com.enali.mapper.EstoqueMapper
import br.com.enali.model.Produto
import br.com.enali.repository.EstoqueRepository
import br.com.enali.repository.ProdutoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class EstoqueService(
    private val repository: EstoqueRepository,
    private val produtoRepository: ProdutoRepository,
    private val mapper: EstoqueMapper
) {

    private fun encontrarProduto(id: Long): Produto {
        return produtoRepository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Produto não encontrado."
            )
        }
    }

    fun criar(request: EstoqueRequestDTO): EstoqueResponseDTO {
        val produto = encontrarProduto(request.produtoId)
        val estoque = mapper.toEntity(request, produto)

        return mapper.toResponse(repository.save(estoque))
    }

    @Transactional(readOnly = true)
    fun listarTodos(): List<EstoqueResponseDTO> {
        return repository.findAll().map {
            mapper.toResponse(it)
        }
    }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Long): EstoqueResponseDTO {
        val estoque = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Estoque não encontrado."
            )
        }

        return mapper.toResponse(estoque)
    }

    @Transactional(readOnly = true)
    fun buscarPorLocalizacao(
        localizacao: String
    ): List<EstoqueResponseDTO> {
        return repository.buscarPorLocalizacao(localizacao.trim()).map {
            mapper.toResponse(it)
        }
    }

    fun atualizar(
        id: Long,
        request: EstoqueRequestDTO
    ): EstoqueResponseDTO {
        val existente = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Estoque não encontrado."
            )
        }

        val produto = encontrarProduto(request.produtoId)

        val atualizado = existente.copy(
            produto = produto,
            quantidadeDisponivel = request.quantidadeDisponivel,
            quantidadeReservada = request.quantidadeReservada,
            estoqueMinimo = request.estoqueMinimo,
            localizacao = request.localizacao.trim(),
            ativo = request.ativo
        )

        return mapper.toResponse(repository.save(atualizado))
    }

    fun remover(id: Long) {
        val estoque = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Estoque não encontrado."
            )
        }

        repository.delete(estoque)
    }
}
