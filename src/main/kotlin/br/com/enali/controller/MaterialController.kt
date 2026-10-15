package br.com.enali.controller

import br.com.enali.dto.MaterialDisponivelRequestDTO
import br.com.enali.dto.MaterialRequestDTO
import br.com.enali.dto.MaterialResponseDTO
import br.com.enali.mapper.MaterialMapper
import br.com.enali.service.MaterialService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/materiais")
class MaterialController(
    private val service: MaterialService,
    private val mapper: MaterialMapper
) {

    @GetMapping
    fun listar(): ResponseEntity<List<MaterialResponseDTO>> {
        val listaDTO = service.listarTodos().map { material ->
            mapper.toDTO(material)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @GetMapping("/disponiveis")
    fun listarDisponiveis(): ResponseEntity<List<MaterialResponseDTO>> {
        val listaDTO = service.listarDisponiveis().map { material ->
            mapper.toDTO(material)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long
    ): ResponseEntity<MaterialResponseDTO> {
        val material = service.procurarPorId(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(mapper.toDTO(material))
    }

    @GetMapping("/buscar/nome")
    fun buscarPorNome(
        @RequestParam nome: String
    ): ResponseEntity<List<MaterialResponseDTO>> {
        val listaDTO = service.buscarPorNome(nome).map { material ->
            mapper.toDTO(material)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @PostMapping
    fun criar(
        @Valid @RequestBody request: MaterialRequestDTO
    ): ResponseEntity<MaterialResponseDTO> {
        val material = mapper.toModel(request)
        val novoMaterial = service.salvar(material)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(novoMaterial))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: MaterialRequestDTO
    ): ResponseEntity<MaterialResponseDTO> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        val materialAtualizado = mapper.toModel(request)
        val materialSalvo = service.atualizar(id, materialAtualizado)

        return ResponseEntity.ok(mapper.toDTO(materialSalvo))
    }

    @PatchMapping("/{id}/disponivel")
    fun alterarDisponivel(
        @PathVariable id: Long,
        @Valid @RequestBody request: MaterialDisponivelRequestDTO
    ): ResponseEntity<MaterialResponseDTO> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        val material = service.alterarDisponivel(id, requireNotNull(request.disponivel))

        return ResponseEntity.ok(mapper.toDTO(material))
    }

    @DeleteMapping("/{id}")
    fun deletar(
        @PathVariable id: Long
    ): ResponseEntity<Map<String, String>> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        service.deletar(id)

        return ResponseEntity.ok(mapOf("mensagem" to "Material excluído com sucesso."))
    }
}
