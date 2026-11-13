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
import br.com.enali.repository.EstoqueRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PedidoService(
    private val repository: PedidoRepository,
    private val clienteRepository: ClienteRepository,
    private val enderecoRepository: EnderecoRepository,
    private val produtoRepository: ProdutoRepository,
    private val estoqueRepository: EstoqueRepository,
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
        
        for (itemDto in request.itens) {
            val produto = produtosMap[itemDto.produtoId]
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto com ID ${itemDto.produtoId} não encontrado.")

            val estoque = estoqueRepository.findByProdutoId(itemDto.produtoId).orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto '${produto.nome}' não possui registro de estoque cadastrado.")
            }

            if (estoque.quantidadeDisponivel < itemDto.quantidade) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estoque insuficiente para o produto '${produto.nome}'. Quantidade solicitada: ${itemDto.quantidade}, disponível em estoque: ${estoque.quantidadeDisponivel}."
                )
            }

            val estoqueAtualizado = estoque.copy(
                quantidadeDisponivel = estoque.quantidadeDisponivel - itemDto.quantidade
            )
            estoqueRepository.save(estoqueAtualizado)
        }
        
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

        devolverItensAoEstoque(pedido)

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

        devolverItensAoEstoque(pedido)

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
    
    private fun devolverItensAoEstoque(pedido: Pedido) {
        for (item in pedido.itens) {
            estoqueRepository.findByProdutoId(item.produtoId).ifPresent { estoque ->
                val estoqueDevolvido = estoque.copy(
                    quantidadeDisponivel = estoque.quantidadeDisponivel + item.quantidade
                )
                estoqueRepository.save(estoqueDevolvido)
            }
        }
    }

}