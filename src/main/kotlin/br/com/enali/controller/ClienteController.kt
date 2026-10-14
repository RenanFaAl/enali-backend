package br.com.enali.controller

import br.com.enali.dto.AlterarSenhaRequestDTO
import br.com.enali.dto.ClienteAtualizacaoRequest
import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.service.ClienteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val service: ClienteService
) {


    @GetMapping("/me")
    fun meusDados(
        authentication: Authentication
    ): ClienteResponse {
        return service.buscarMeusDados(authentication.name)
    }

    @PutMapping("/me")
    fun atualizarMeusDados(
        authentication: Authentication,
        @Valid @RequestBody request: ClienteAtualizacaoRequest
    ): ClienteResponse {
        return service.atualizarMeusDados(
            authentication.name,
            request
        )
    }

    @PatchMapping("/me/senha")
    fun alterarMinhaSenha(
        authentication: Authentication,
        @Valid @RequestBody request: AlterarSenhaRequestDTO
    ): ResponseEntity<Map<String, String>> {

        if (request.senhaNova != request.confirmarSenha) {
            return ResponseEntity.badRequest().body(
                mapOf(
                    "mensagem" to
                            "A confirmação da senha não corresponde à nova senha."
                )
            )
        }

        service.alterarMinhaSenha(
            emailAutenticado = authentication.name,
            senhaAtual = request.senhaAtual,
            senhaNova = request.senhaNova
        )

        return ResponseEntity.ok(
            mapOf("mensagem" to "Senha alterada com sucesso.")
        )
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

    @PatchMapping("/{id}/ativo")
    fun alterarAtivo(
        @PathVariable id: Long,
        @RequestBody request: AlterarAtivoRequest
    ): ClienteResponse {
        return service.alterarAtivo(id, request.ativo)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remover(@PathVariable id: Long) {
        service.remover(id)
    }
}

data class AlterarAtivoRequest(
    val ativo: Boolean
)