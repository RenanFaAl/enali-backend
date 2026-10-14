package br.com.enali.mapper

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.model.Pedido
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PedidoMapper {

    fun toEntity(
        request: PedidoRequest,
        numeroPedido: String
    ): Pedido {
        return Pedido(
            numeroPedido = numeroPedido,
            valorTotal = request.valorTotal,
            dataPedido = LocalDateTime.now(),
            status = "PENDENTE",
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