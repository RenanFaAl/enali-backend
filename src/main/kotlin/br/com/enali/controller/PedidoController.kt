package br.com.enali.controller

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.service.ClienteService
import br.com.enali.service.PedidoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pedidos")
class PedidoController(
    private val pedidoService: PedidoService,
    private val clienteService: ClienteService
) {

    @PostMapping("/clientes/me/pedidos")
    fun criar(
        authentication: Authentication,
        @Valid @RequestBody request: PedidoRequest
    ): ResponseEntity<PedidoResponse> {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        val clienteId = clienteResponse.id

        val pedido = pedidoService.criar(clienteId, request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(pedido)
    }
    
    @GetMapping("/clientes/me/pedidos")
    fun listarMeusPedidos(authentication: Authentication): List<PedidoResponse> {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        return pedidoService.listarPorCliente(clienteResponse.id)
    }

    @GetMapping("/clientes/me/pedidos/{id}")
    fun buscarMeuPedidoPorId(
        authentication: Authentication,
        @PathVariable id: Long
    ): PedidoResponse {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        return pedidoService.buscarPorIdECliente(id, clienteResponse.id)
    }

    @GetMapping("/clientes/me/pedidos/buscar")
    fun buscarMeuPedidoPorNumero(
        authentication: Authentication,
        @RequestParam numero: String
    ): List<PedidoResponse> {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        return pedidoService.buscarPorNumeroECliente(numero, clienteResponse.id)
    }

    @PatchMapping("/clientes/me/pedidos/{id}/cancelar")
    fun cancelarMeuPedido(
        authentication: Authentication,
        @PathVariable id: Long
    ): PedidoResponse {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        return pedidoService.cancelarParaCliente(id, clienteResponse.id)
    }
    
    @GetMapping
    fun listar(): ResponseEntity<List<PedidoResponse>> {
        return ResponseEntity.ok(
            pedidoService.listarTodos()
        )
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long
    ): ResponseEntity<PedidoResponse> {
        return ResponseEntity.ok(
            pedidoService.buscarPorId(id)
        )
    }

    @GetMapping("/buscar")
    fun buscarPorNumero(
        @RequestParam numero: String
    ): ResponseEntity<List<PedidoResponse>> {
        return ResponseEntity.ok(
            pedidoService.buscarPorNumero(numero)
        )
    }

    @PatchMapping("/{id}/cancelar")
    fun cancelar(
        @PathVariable id: Long
    ): ResponseEntity<PedidoResponse> {
        return ResponseEntity.ok(
            pedidoService.cancelar(id)
        )
    }
}