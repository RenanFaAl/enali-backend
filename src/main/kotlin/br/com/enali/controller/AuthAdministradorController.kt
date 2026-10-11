package br.com.enali.controller

import br.com.enali.dto.LoginRequestDTO
import br.com.enali.dto.LoginResponseDTO
import br.com.enali.service.AuthAdministradorService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/admin")
class AuthAdministradorController(
    private val authService: AuthAdministradorService
) {
    @PostMapping("/signin")
    fun login(
        @Valid @RequestBody request: LoginRequestDTO
    ): ResponseEntity<LoginResponseDTO> {
        val resposta = authService.login(request)
        return ResponseEntity.ok(resposta)
    }
}
