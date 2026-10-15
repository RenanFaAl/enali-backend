
package br.com.enali.controller

import br.com.enali.dto.EstoqueRequestDTO
import br.com.enali.dto.EstoqueResponseDTO
import br.com.enali.service.EstoqueService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/estoques")
class EstoqueController(
    private val estoqueService: EstoqueService
) {

    @PostMapping
    fun criar(
        @Valid @RequestBody request: EstoqueRequestDTO
    ): ResponseEntity<EstoqueResponseDTO> {
        val estoque = estoqueService.criar(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(estoque)
    }

    @GetMapping
    fun listar(): ResponseEntity<List<EstoqueResponseDTO>> {
        return ResponseEntity.ok(
            estoqueService.listarTodos()
        )
    }

    @GetMapping("/buscar")
    fun buscarPorLocalizacao(
        @RequestParam localizacao: String
    ): ResponseEntity<List<EstoqueResponseDTO>> {
        return ResponseEntity.ok(
            estoqueService.buscarPorLocalizacao(localizacao)
        )
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long
    ): ResponseEntity<EstoqueResponseDTO> {
        return ResponseEntity.ok(
            estoqueService.buscarPorId(id)
        )
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: EstoqueRequestDTO
    ): ResponseEntity<EstoqueResponseDTO> {
        return ResponseEntity.ok(
            estoqueService.atualizar(id, request)
        )
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remover(@PathVariable id: Long) {
        estoqueService.remover(id)
    }
}
