package br.com.enali.mapper

import br.com.enali.dto.ItemPedidoResponse
import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.model.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class PedidoMapper {

    fun toEntity(
        request: PedidoRequest,
        numeroPedido: String,
        cliente: Cliente,
        enderecoOriginal: Endereco,
        produtosMap: Map<Long, Produto>
    ): Pedido {
        val enderecoSnapshot = EnderecoSnapshot(
            rua = enderecoOriginal.rua,
            numero = enderecoOriginal.numero,
            complemento = enderecoOriginal.complemento,
            bairro = enderecoOriginal.bairro,
            cidade = enderecoOriginal.cidade,
            estado = enderecoOriginal.estado,
            siglaEstado = enderecoOriginal.siglaEstado,
            cep = enderecoOriginal.cep,
            pais = enderecoOriginal.pais,
            referencia = enderecoOriginal.referencia
        )

        var valorTotalCalculado = BigDecimal.ZERO

        val itensSnapshot = request.itens.map { itemDto ->
            val produto = produtosMap[itemDto.produtoId] 
                ?: throw IllegalArgumentException("Produto com ID ${itemDto.produtoId} não encontrado.")

            val fatorDesconto = BigDecimal.ONE.subtract(BigDecimal.valueOf(produto.desconto / 100.0))
            val precoComDesconto = produto.preco.multiply(fatorDesconto)
            val subtotal = precoComDesconto.multiply(BigDecimal.valueOf(itemDto.quantidade.toLong()))

            valorTotalCalculado = valorTotalCalculado.add(subtotal)

            ItemPedidoSnapshot(
                produtoId = produto.id,
                nome = produto.nome,
                descricao = produto.descricao,
                precoUnitario = produto.preco,
                desconto = produto.desconto,
                quantidade = itemDto.quantidade,
                subtotal = subtotal
            )
        }

        return Pedido(
            numeroPedido = numeroPedido,
            valorTotal = valorTotalCalculado.toDouble(),
            dataPedido = LocalDateTime.now(),
            status = "PENDENTE",
            observacoes = request.observacoes,
            cliente = cliente,
            clienteId = cliente.id,
            clienteNome = cliente.nome,
            clienteEmail = cliente.email,
            clienteCpf = cliente.cpf,
            enderecoEntrega = enderecoSnapshot,
            itens = itensSnapshot
        )
    }

    fun toResponse(pedido: Pedido): PedidoResponse {
        return PedidoResponse(
            id = pedido.id,
            numeroPedido = pedido.numeroPedido,
            valorTotal = pedido.valorTotal,
            dataPedido = pedido.dataPedido,
            status = pedido.status,
            observacoes = pedido.observacoes,
            clienteNome = pedido.clienteNome,
            clienteEmail = pedido.clienteEmail,
            clienteCpf = pedido.clienteCpf,
            enderecoEntrega = EnderecoMapper.toDTO(
                Endereco(
                    rua = pedido.enderecoEntrega.rua,
                    numero = pedido.enderecoEntrega.numero,
                    complemento = pedido.enderecoEntrega.complemento,
                    bairro = pedido.enderecoEntrega.bairro,
                    cidade = pedido.enderecoEntrega.cidade,
                    estado = pedido.enderecoEntrega.estado,
                    siglaEstado = pedido.enderecoEntrega.siglaEstado,
                    cep = pedido.enderecoEntrega.cep,
                    pais = pedido.enderecoEntrega.pais,
                    referencia = pedido.enderecoEntrega.referencia
                )
            ),
            itens = pedido.itens.map { item ->
                ItemPedidoResponse(
                    produtoId = item.produtoId,
                    nome = item.nome,
                    descricao = item.descricao,
                    precoUnitario = item.precoUnitario,
                    desconto = item.desconto,
                    quantidade = item.quantidade,
                    subtotal = item.subtotal
                )
            }
        )
    }
}