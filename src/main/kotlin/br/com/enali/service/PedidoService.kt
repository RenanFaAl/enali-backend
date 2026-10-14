package br.com.enali.service

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.mapper.PedidoMapper
import br.com.enali.repository.PedidoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PedidoService(
    private val repository: PedidoRepository,
    private val mapper: PedidoMapper
) {


    fun criar(request: PedidoRequest): PedidoResponse {
        val dataAtual = LocalDate.now()
        val prefixo = dataAtual.format(DateTimeFormatter.BASIC_ISO_DATE)

        val pedidosDoDia = repository.findByNumeroPedidoStartingWith(prefixo)

        val proximoNumero = pedidosDoDia
            .mapNotNull { it.numeroPedido.removePrefix(prefixo).toIntOrNull() }
            .maxOrNull()
            ?.plus(1) ?: 1

        val numeroPedido = prefixo + proximoNumero.toString().padStart(5, '0')

        val pedido = mapper.toEntity(
            request = request,
            numeroPedido = numeroPedido
        )

        return mapper.toResponse(repository.save(pedido))
    }


    fun listarTodos(): List<PedidoResponse> {
        return repository.findAll().map {
            mapper.toResponse(it)
        }
    }

    fun buscarPorId(id: Long): PedidoResponse {
        val pedido = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado"
            )
        }

        return mapper.toResponse(pedido)
    }

    fun buscarPorNumero(numero: String): List<PedidoResponse> {
        return repository.buscarPorNumero(numero).map {
            mapper.toResponse(it)
        }
    }

    fun atualizar(
        id: Long,
        request: PedidoRequest
    ): PedidoResponse {
        val existente = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado"
            )
        }

        val atualizado = existente.copy(
            valorTotal = request.valorTotal,
            observacoes = request.observacoes
        )

        return mapper.toResponse(repository.save(atualizado))
    }

    fun cancelar(id: Long): PedidoResponse {
        val pedido = repository.findById(id).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Pedido não encontrado"
            )
        }

        if (pedido.status.equals("CANCELADO", ignoreCase = true)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Este pedido já foi cancelado"
            )
        }

        val pedidoCancelado = pedido.copy(
            status = "CANCELADO"
        )

        return mapper.toResponse(repository.save(pedidoCancelado))
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