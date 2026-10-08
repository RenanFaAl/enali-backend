package br.com.enali.controller

import br.com.enali.model.Administrador
import br.com.enali.service.AdministradorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/administradores")
class AdministradorController(
    private val administradorService: AdministradorService
) {

    @GetMapping
    fun listar(): List<Administrador> {
      return administradorService.listarTodos()
    }
    
    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Administrador> {
      val admin = administradorService.procurarPorId(id)
      return if (admin != null) {
        ResponseEntity.ok(admin)
      } else {
        ResponseEntity.notFound().build()
      }
    }
    
    @GetMapping("/buscar")
    fun buscarPorEmail(@RequestParam email: String): ResponseEntity<Administrador> {
      val admin = administradorService.procurarPorEmail(email)
      return if (admin != null) {
        ResponseEntity.ok(admin)
      } else {
        ResponseEntity.notFound().build()
      }
    }

    @PostMapping
    fun criar(@RequestBody administrador: Administrador): ResponseEntity<Administrador> {
      val novoAdmin = administradorService.salvar(administrador)
      return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin)
    }
    
    @PutMapping("/{id}")
    fun atualizar(
      @PathVariable id: Long,
      @RequestBody administrador: Administrador
    ): ResponseEntity<Administrador> {
      return try {
        val adminAtualizado = administradorService.atualizar(id, administrador)
        ResponseEntity.ok(adminAtualizado)
      } catch (e: IllegalArgumentException) {
        ResponseEntity.notFound().build()
      }
    }

    @DeleteMapping("/{id}")
    fun deletar(@PathVariable id: Long): ResponseEntity<Void> {
      return if (administradorService.existe(id)) {
        administradorService.deletar(id)
        ResponseEntity.noContent().build()
      } else {
        ResponseEntity.notFound().build()
      }
    }
}