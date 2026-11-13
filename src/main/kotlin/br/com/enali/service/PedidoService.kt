package br.com.enali.service

import br.com.enali.dto.ItemPedidoRequest
import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.mapper.PedidoMapper
import br.com.enali.model.Estoque
import br.com.enali.model.ItemPedido
import br.com.enali.repository.ClienteRepository
import br.com.enali.repository.EnderecoRepository
import br.com.enali.repository.EstoqueRepository
import br.com.enali.repository.PedidoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PedidoService(
    private val repository: PedidoRepository,
    private val mapper: PedidoMapper,
    private val clienteRepository: ClienteRepository,
    private val enderecoRepository: EnderecoRepository,
    private val estoqueRepository: EstoqueRepository
) {

    // UC05 - Realizar Pedido. Cada "if" corresponde a um nó predicado do GFC (ComplexGraph).
    @Transactional
    fun criar(clienteId: Long, request: PedidoRequest): PedidoResponse {
        // RN17: somente clientes autenticados (e ativos) podem realizar pedidos
        val cliente = clienteRepository.findByIdAndAtivoTrue(clienteId)
        if (cliente == null) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Somente clientes autenticados podem realizar pedidos.")
        }

        // RN18: o endereço de entrega deve ser um endereço ativo do próprio cliente
        val endereco = enderecoRepository.findByIdAndClienteIdAndAtivoTrue(request.enderecoId, clienteId)
        if (endereco == null) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Cadastre ou selecione um endereço de entrega válido.")
        }

        if (request.itens.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O pedido deve conter pelo menos um item.")
        }

        val quantidades = somarQuantidadesPorProduto(request.itens)
        val itens = mutableListOf<ItemPedido>()
        val estoquesReservados = mutableListOf<Estoque>()
        val indisponiveis = mutableListOf<String>()

        for ((produtoId, quantidade) in quantidades) {
            val estoque = estoqueRepository.findFirstByProdutoIdOrderByIdAsc(produtoId)
            if (estoque == null) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $produtoId não encontrado.")
            }

            // Fluxo de exceção b: produto desativado durante a compra
            if (!estoque.produto.disponivel) {
                throw ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "O produto ${estoque.produto.nome} foi desativado. Remova-o do carrinho.")
            }

            // RN26: verifica a disponibilidade antes de confirmar
            if (estoque.possuiDisponibilidade(quantidade)) {
                itens.add(ItemPedido.de(estoque.produto, quantidade))
                estoquesReservados.add(estoque.reservar(quantidade))
            } else {
                indisponiveis.add(estoque.produto.nome)
            }
        }

        // Fluxo de exceção a: informa quais produtos estão sem estoque suficiente
        if (indisponiveis.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para: ${indisponiveis.joinToString(", ")}.")
        }

        // RN27: registra produtos, quantidades, valores e endereço de entrega
        val pedido = mapper.toEntity(request, gerarNumeroPedido(), cliente, endereco, itens)

        // Fluxo de exceção c: falha no cálculo do valor total
        if (pedido.valorTotal <= 0.0) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Não foi possível calcular o valor total do pedido.")
        }

        // Fluxo de exceção d: o pedido é gravado antes da reserva; se falhar, nenhuma baixa é feita
        val salvo = repository.save(pedido)
        // RN54: Estoque.reservar() já impediu saldo negativo
        estoqueRepository.saveAll(estoquesReservados)

        return mapper.toResponse(salvo)
    }

    // O mesmo produto repetido no carrinho vira uma única linha com a soma das quantidades
    private fun somarQuantidadesPorProduto(itens: List<ItemPedidoRequest>): Map<Long, Int> {
        return itens.groupBy { it.produtoId }.mapValues { (_, iguais) -> iguais.sumOf { it.quantidade } }
    }

    private fun gerarNumeroPedido(): String {
        val prefixo = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)

        val proximoNumero = repository.findByNumeroPedidoStartingWith(prefixo)
            .mapNotNull { it.numeroPedido.removePrefix(prefixo).toIntOrNull() }
            .maxOrNull()
            ?.plus(1) ?: 1

        return prefixo + proximoNumero.toString().padStart(5, '0')
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

    @Transactional
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

        // Devolve ao estoque o que foi reservado na criação do pedido
        val estoquesLiberados = pedido.itens.mapNotNull { item ->
            estoqueRepository.findFirstByProdutoIdOrderByIdAsc(item.produto.id)?.liberar(item.quantidade)
        }
        estoqueRepository.saveAll(estoquesLiberados)

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
