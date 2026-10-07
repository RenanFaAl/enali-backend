package com.example.ecommerce.controller

import com.example.ecommerce.dto.PedidoRequest
import com.example.ecommerce.dto.PedidoResponse
import com.example.ecommerce.model.Pedido
import com.example.ecommerce.repository.PedidoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/pedidos")
class PedidoController(
    private val pedidoRepository: PedidoRepository
) {

    @PostMapping
    fun criar(@RequestBody request: PedidoRequest): ResponseEntity<PedidoResponse> {

    val pedido = Pedido(
        numeroPedido = request.numeroPedido,
        valorTotal = request.valorTotal,
        dataPedido = LocalDateTime.now(),
        status = request.status,
        observacoes = request.observacoes
    )

    val pedidoSalvo = pedidoRepository.save(pedido)

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(pedidoSalvo.toResponse())
    }

    @GetMapping
    fun listar(): ResponseEntity<List<PedidoResponse>> {

        val pedidos = pedidoRepository.findAll()
            .map { it.toResponse() }

        return ResponseEntity.ok(pedidos)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<PedidoResponse> {

        val pedido = pedidoRepository.findById(id)

        if (pedido.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(pedido.get().toResponse())
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {

        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }

        pedidoRepository.deleteById(id)

        return ResponseEntity.noContent().build()
    }

    private fun Pedido.toResponse(): PedidoResponse {
        return PedidoResponse(
            id = id,
            numeroPedido = numeroPedido,
            valorTotal = valorTotal,
            dataPedido = dataPedido,
            status = status,
            observacoes = observacoes
        )
    }
}