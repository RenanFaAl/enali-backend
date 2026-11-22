package br.com.enali.controller

import br.com.enali.dto.ProdutoImagemResponseDTO
import br.com.enali.dto.ProdutoRequestDTO
import br.com.enali.dto.ProdutoResponseDTO
import br.com.enali.service.ProdutoService
import jakarta.validation.Valid
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.TimeUnit

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
    
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun criar(
        @Valid @RequestPart("produto") request: ProdutoRequestDTO,
        @RequestPart("imagens", required = false) imagens: List<MultipartFile>?
    ): ResponseEntity<ProdutoResponseDTO> {
        val produto = produtoService.criarComImagens(request, imagens)

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

    @PostMapping("/{id}/imagens", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun adicionarImagem(
        @PathVariable id: Long,
        @RequestParam("arquivo") arquivo: MultipartFile,
        @RequestParam("ordem", required = false) ordem: Int?
    ): ResponseEntity<ProdutoImagemResponseDTO> {
        val criada = produtoService.adicionarImagem(id, arquivo, ordem)
        return ResponseEntity.status(HttpStatus.CREATED).body(criada)
    }

    @GetMapping("/{id}/imagens")
    fun listarImagens(@PathVariable id: Long): ResponseEntity<List<ProdutoImagemResponseDTO>> {
        return ResponseEntity.ok(produtoService.listarImagens(id))
    }

    @GetMapping("/{id}/imagens/{imagemId}")
    fun buscarImagem(
        @PathVariable id: Long,
        @PathVariable imagemId: Long
    ): ResponseEntity<ByteArray> {
        val imagem = produtoService.buscarImagemBinaria(id, imagemId)
        val mediaType = MediaType.parseMediaType(imagem.tipoMime)

        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
            .body(imagem.bytesOrDados())
    }

    @DeleteMapping("/{id}/imagens/{imagemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removerImagem(
        @PathVariable id: Long,
        @PathVariable imagemId: Long
    ) {
        produtoService.removerImagem(id, imagemId)
    }
}

private fun br.com.enali.model.ProdutoImagem.bytesOrDados(): ByteArray = this.dados