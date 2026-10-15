
package br.com.enali.controller

import br.com.enali.dto.ProdutoRequestDTO
import br.com.enali.dto.ProdutoResponseDTO
import br.com.enali.service.ProdutoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/produtos")
class ProdutoController(
    private val produtoService: ProdutoService
) {

    @PostMapping
    fun criar(
        @Valid @RequestBody request: ProdutoRequestDTO
    ): ResponseEntity<ProdutoResponseDTO> {
        val produto = produtoService.criar(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(produto)
    }

    @GetMapping
    fun listar(): ResponseEntity<List<ProdutoResponseDTO>> {
        return ResponseEntity.ok(
            produtoService.listarTodos()
        )
    }

    @GetMapping("/buscar")
    fun buscarPorNome(
        @RequestParam nome: String
    ): ResponseEntity<List<ProdutoResponseDTO>> {
        return ResponseEntity.ok(
            produtoService.buscarPorNome(nome)
        )
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long
    ): ResponseEntity<ProdutoResponseDTO> {
        return ResponseEntity.ok(
            produtoService.buscarPorId(id)
        )
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProdutoRequestDTO
    ): ResponseEntity<ProdutoResponseDTO> {
        return ResponseEntity.ok(
            produtoService.atualizar(id, request)
        )
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remover(@PathVariable id: Long) {
        produtoService.remover(id)
    }
}
