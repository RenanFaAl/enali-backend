
package br.com.enali.service

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.mapper.PedidoMapper
import br.com.enali.repository.PedidoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PedidoService(
    private val repository: PedidoRepository,
    private val mapper: PedidoMapper
) {

    fun criar(request: PedidoRequest): PedidoResponse {
        val pedido = mapper.toEntity(request)
        return mapper.toResponse(repository.save(pedido))
    }

    fun listarTodos(): List<PedidoResponse> {
        return repository.findAll().map { mapper.toResponse(it) }
    }

    fun buscarPorId(id: Long): PedidoResponse {
        val pedido = repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado")
        }
        return mapper.toResponse(pedido)
    }

    fun buscarPorNumero(numero: String): List<PedidoResponse> {
        return repository.buscarPorNumero(numero).map { mapper.toResponse(it) }
    }

    fun atualizar(id: Long, request: PedidoRequest): PedidoResponse {
        val existente = repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado")
        }

        val atualizado = existente.copy(
            numeroPedido = request.numeroPedido,
            valorTotal = request.valorTotal,
            dataPedido = request.dataPedido,
            status = request.status,
            observacoes = request.observacoes
        )

        return mapper.toResponse(repository.save(atualizado))
    }

    fun remover(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado"
            )
        }

        repository.deleteById(id)
    }
}
