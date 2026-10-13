
package br.com.enali.controller

import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.service.ClienteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val service: ClienteService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criar(
        @Valid @RequestBody request: ClienteRequest
    ): ClienteResponse {
        return service.criar(request)
    }

    @GetMapping
    fun listarTodos(): List<ClienteResponse> {
        return service.listarTodos()
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ClienteResponse {
        return service.buscarPorId(id)
    }

    @GetMapping("/buscar")
    fun buscarPorNome(@RequestParam nome: String): List<ClienteResponse> {
        return service.buscarPorNome(nome)
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: ClienteRequest
    ): ClienteResponse {
        return service.atualizar(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remover(@PathVariable id: Long) {
        service.remover(id)
    }
}
