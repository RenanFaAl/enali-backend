package br.com.enali.service

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.mapper.PedidoMapper
import br.com.enali.model.EnderecoSnapshot
import br.com.enali.model.ItemPedidoSnapshot
import br.com.enali.model.Pedido
import br.com.enali.repository.ClienteRepository
import br.com.enali.repository.EnderecoRepository
import br.com.enali.repository.PedidoRepository
import br.com.enali.repository.ProdutoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PedidoService(
    private val repository: PedidoRepository,
    private val clienteRepository: ClienteRepository,
    private val enderecoRepository: EnderecoRepository,
    private val produtoRepository: ProdutoRepository,
    private val mapper: PedidoMapper
) {

    fun criar(clienteId: Long, request: PedidoRequest): PedidoResponse {
        val cliente = clienteRepository.findById(clienteId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.")
        }

        val enderecoOriginal = enderecoRepository.findById(request.enderecoId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.")
        }

        val idsProdutos = request.itens.map { item -> item.produtoId }
        val produtosMap = produtoRepository.findAllById(idsProdutos).associateBy { it.id }

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
            numeroPedido = numeroPedido,
            cliente = cliente,
            enderecoOriginal = enderecoOriginal,
            produtosMap = produtosMap
        )

        return mapper.toResponse(repository.save(pedido))
    }
    
    fun listarPorCliente(clienteId: Long): List<PedidoResponse> {
        return repository.findByClienteId(clienteId).map { mapper.toResponse(it) }
    }

    fun buscarPorIdECliente(id: Long, clienteId: Long): PedidoResponse {
        val pedido = repository.findByIdAndClienteId(id, clienteId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado ou não pertence a você.")
        return mapper.toResponse(pedido)
    }

    fun buscarPorNumeroECliente(numero: String, clienteId: Long): List<PedidoResponse> {
        return repository.buscarPorClienteENumero(clienteId, numero).map { mapper.toResponse(it) }
    }

    fun cancelarParaCliente(id: Long, clienteId: Long): PedidoResponse {
        val pedido = repository.findByIdAndClienteId(id, clienteId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado ou não pertence a você.")

        if (pedido.status.equals("CANCELADO", ignoreCase = true)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Este pedido já foi cancelado.")
        }

        val pedidoCancelado = pedido.copy(status = "CANCELADO")
        return mapper.toResponse(repository.save(pedidoCancelado))
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