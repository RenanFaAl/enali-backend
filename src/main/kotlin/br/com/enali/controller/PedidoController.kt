package br.com.enali.controller

import br.com.enali.dto.PedidoRequest
import br.com.enali.dto.PedidoResponse
import br.com.enali.service.PedidoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pedidos")
class PedidoController(
    private val pedidoService: PedidoService
) {

    @PostMapping
    fun criar(
        @Valid @RequestBody request: PedidoRequest
    ): ResponseEntity<PedidoResponse> {
        val pedido = pedidoService.criar(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(pedido)
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