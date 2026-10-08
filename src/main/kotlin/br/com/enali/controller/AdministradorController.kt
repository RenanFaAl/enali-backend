package br.com.enali.controller

import br.com.enali.dto.AdministradorResponseDTO
import br.com.enali.mapper.AdministradorMapper
import br.com.enali.model.Administrador
import br.com.enali.service.AdministradorService
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
        return try {
            val listaAdministradores = service.listarTodos()
            val listaDTO = listaAdministradores.map { admin -> mapper.toDTO(admin) }
            ResponseEntity.ok(listaDTO)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(emptyList())
        }
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<AdministradorResponseDTO> {
        return try {
            val admin = service.procurarPorId(id)
            if (admin != null) {
                ResponseEntity.ok(mapper.toDTO(admin))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }

    }

    @GetMapping("/buscar")
    fun buscarPorEmail(@RequestParam email: String): ResponseEntity<AdministradorResponseDTO> {
        return try {
            val admin = service.procurarPorEmail(email)
            if (admin != null) {
                ResponseEntity.ok(mapper.toDTO(admin))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun criar(@RequestBody administrador: Administrador): ResponseEntity<AdministradorResponseDTO> {
        return try {
            val novoAdmin = service.salvar(administrador)
            ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(novoAdmin))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }

    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody administrador: Administrador
    ): ResponseEntity<AdministradorResponseDTO> {
        return try {
            val adminAtualizado = service.atualizar(id, administrador)
            ResponseEntity.ok(mapper.toDTO(adminAtualizado))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletar(@PathVariable id: Long): ResponseEntity<String> {
        return try {
            if (service.existe(id)) {
                service.deletar(id)
                ResponseEntity.ok().body("Administrador excluido com sucesso.")
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Erro ao deletar administrador")
        }
    }
}