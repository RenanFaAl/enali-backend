package br.com.enali.controller

import br.com.enali.dto.FornecedorAtivoRequestDTO
import br.com.enali.dto.FornecedorRequestDTO
import br.com.enali.dto.FornecedorResponseDTO
import br.com.enali.mapper.FornecedorMapper
import br.com.enali.service.FornecedorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fornecedores")
class FornecedorController(
    private val service: FornecedorService,
    private val mapper: FornecedorMapper
) {

    @GetMapping
    fun listar(): ResponseEntity<List<FornecedorResponseDTO>> {
        val listaDTO = service.listarTodos().map { fornecedor ->
            mapper.toDTO(fornecedor)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long
    ): ResponseEntity<FornecedorResponseDTO> {
        val fornecedor = service.procurarPorId(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(mapper.toDTO(fornecedor))
    }

    @GetMapping("/buscar/cnpj")
    fun buscarPorCnpj(
        @RequestParam cnpj: String
    ): ResponseEntity<FornecedorResponseDTO> {
        val fornecedor = service.procurarPorCnpj(mapper.somenteDigitos(cnpj))
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(mapper.toDTO(fornecedor))
    }

    @GetMapping("/buscar/nome")
    fun buscarPorNome(
        @RequestParam nome: String
    ): ResponseEntity<List<FornecedorResponseDTO>> {
        val listaDTO = service.buscarPorNome(nome).map { fornecedor ->
            mapper.toDTO(fornecedor)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @PostMapping
    fun criar(
        @Valid @RequestBody request: FornecedorRequestDTO
    ): ResponseEntity<FornecedorResponseDTO> {
        val fornecedor = mapper.toModel(request)
        val novoFornecedor = service.salvar(fornecedor)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(novoFornecedor))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: FornecedorRequestDTO
    ): ResponseEntity<FornecedorResponseDTO> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        val fornecedorAtualizado = mapper.toModel(request)
        val fornecedorSalvo = service.atualizar(id, fornecedorAtualizado)

        return ResponseEntity.ok(mapper.toDTO(fornecedorSalvo))
    }

    @PatchMapping("/{id}/ativo")
    fun alterarAtivo(
        @PathVariable id: Long,
        @Valid @RequestBody request: FornecedorAtivoRequestDTO
    ): ResponseEntity<FornecedorResponseDTO> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        val fornecedor = service.alterarAtivo(id, requireNotNull(request.ativo))

        return ResponseEntity.ok(mapper.toDTO(fornecedor))
    }

    @DeleteMapping("/{id}")
    fun deletar(
        @PathVariable id: Long
    ): ResponseEntity<Map<String, String>> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        service.deletar(id)

        return ResponseEntity.ok(mapOf("mensagem" to "Fornecedor excluído com sucesso."))
    }
}
