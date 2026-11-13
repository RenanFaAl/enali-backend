package br.com.enali.mapper

import br.com.enali.dto.ItemPedidoResponse
import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.model.Cliente
import br.com.enali.model.Endereco
import br.com.enali.model.ItemPedido
import br.com.enali.model.Pedido
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PedidoMapper {

    fun toEntity(
        request: PedidoRequest,
        numeroPedido: String,
        cliente: Cliente,
        endereco: Endereco,
        itens: List<ItemPedido>
    ): Pedido {
        val pedido = Pedido(
            numeroPedido = numeroPedido,
            valorTotal = 0.0,
            dataPedido = LocalDateTime.now(),
            status = "PENDENTE",
            observacoes = request.observacoes,
            cliente = cliente,
            itens = itens,
            // RN28: copia o endereço em vez de referenciá-lo
            cepEntrega = endereco.cep,
            ruaEntrega = endereco.rua,
            numeroEntrega = endereco.numero,
            bairroEntrega = endereco.bairro,
            cidadeEntrega = endereco.cidade,
            estadoEntrega = endereco.siglaEstado
        )

        return pedido.copy(valorTotal = pedido.calcularTotal())
    }

    fun toResponse(pedido: Pedido): PedidoResponse {
        return PedidoResponse(
            id = pedido.id,
            numeroPedido = pedido.numeroPedido,
            valorTotal = pedido.valorTotal,
            dataPedido = pedido.dataPedido,
            status = pedido.status,
            observacoes = pedido.observacoes,
            clienteId = pedido.cliente?.id,
            itens = pedido.itens.map {
                ItemPedidoResponse(
                    produtoId = it.produto.id,
                    produtoNome = it.produto.nome,
                    quantidade = it.quantidade,
                    precoUnitario = it.precoUnitario
                )
            },
            cepEntrega = pedido.cepEntrega,
            ruaEntrega = pedido.ruaEntrega,
            numeroEntrega = pedido.numeroEntrega,
            bairroEntrega = pedido.bairroEntrega,
            cidadeEntrega = pedido.cidadeEntrega,
            estadoEntrega = pedido.estadoEntrega
        )
    }
}
