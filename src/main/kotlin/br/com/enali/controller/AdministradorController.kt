package br.com.enali.controller

import br.com.enali.dto.AdministradorAtualizacaoRequestDTO
import br.com.enali.dto.AdministradorRequestDTO
import br.com.enali.dto.AdministradorResponseDTO
import br.com.enali.dto.AlterarSenhaRequestDTO
import br.com.enali.mapper.AdministradorMapper
import br.com.enali.model.Administrador
import br.com.enali.service.AdministradorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdministradorController(
    private val service: AdministradorService,
    private val mapper: AdministradorMapper
) {

    @GetMapping
    fun listar(): ResponseEntity<List<AdministradorResponseDTO>> {
        val listaAdministradores = service.listarTodos()
        val listaDTO = listaAdministradores.map { admin ->
            mapper.toDTO(admin)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Long
    ): ResponseEntity<AdministradorResponseDTO> {
        val admin = service.procurarPorId(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(mapper.toDTO(admin))
    }

    @GetMapping("/buscar/email")
    fun buscarPorEmail(
        @RequestParam email: String
    ): ResponseEntity<AdministradorResponseDTO> {
        val admin = service.procurarPorEmail(email)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(mapper.toDTO(admin))
    }

    @GetMapping("/buscar/nome")
    fun buscarPorNome(
        @RequestParam nome: String
    ): ResponseEntity<List<AdministradorResponseDTO>> {
        val administradores = service.buscarPorNome(nome)
        val listaDTO = administradores.map { admin ->
            mapper.toDTO(admin)
        }

        return ResponseEntity.ok(listaDTO)
    }

    @PostMapping
    fun criar(
        @Valid @RequestBody request: AdministradorRequestDTO
    ): ResponseEntity<AdministradorResponseDTO> {
        val administrador = mapper.toModel(request)
        val novoAdmin = service.salvar(administrador)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(novoAdmin))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: AdministradorAtualizacaoRequestDTO
    ): ResponseEntity<AdministradorResponseDTO> {
        val existente = service.procurarPorId(id)
            ?: return ResponseEntity.notFound().build()

        val adminAtualizado = mapper.atualizarModel(existente, request)
        val adminSalvo = service.atualizar(id, adminAtualizado)

        return ResponseEntity.ok(mapper.toDTO(adminSalvo))
    }

    @PatchMapping("/{id}/senha")
    fun alterarSenha(
        @PathVariable id: Long,
        @Valid @RequestBody request: AlterarSenhaRequestDTO
    ): ResponseEntity<Map<String, String>> {

        if (request.senhaNova != request.confirmarSenha) {
            return ResponseEntity.badRequest()
                .body(mapOf("mensagem" to "A confirmação da senha não corresponde à nova senha."))
        }

        service.alterarSenha(
            id = id,
            senhaAtual = request.senhaAtual,
            senhaNova = request.senhaNova
        )

        return ResponseEntity.ok(
            mapOf("mensagem" to "Senha alterada com sucesso.")
        )
    }


    @DeleteMapping("/{id}")
    fun deletar(
        @PathVariable id: Long
    ): ResponseEntity<Map<String, String>> {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build()
        }

        service.deletar(id)

        return ResponseEntity.ok(mapOf("mensagem" to "Administrador excluído com sucesso."))
    }
}