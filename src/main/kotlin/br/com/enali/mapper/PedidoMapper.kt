
package br.com.enali.mapper

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.model.Pedido
import org.springframework.stereotype.Component

@Component
class PedidoMapper {

    fun toEntity(request: PedidoRequest): Pedido {
        return Pedido(
            numeroPedido = request.numeroPedido,
            valorTotal = request.valorTotal,
            dataPedido = request.dataPedido,
            status = request.status,
            observacoes = request.observacoes
        )
    }

    fun toResponse(pedido: Pedido): PedidoResponse {
        return PedidoResponse(
            id = pedido.id,
            numeroPedido = pedido.numeroPedido,
            valorTotal = pedido.valorTotal,
            dataPedido = pedido.dataPedido,
            status = pedido.status,
            observacoes = pedido.observacoes
        )
    }
}
