package br.com.enali.controller

import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.dto.LoginRequestDTO
import br.com.enali.dto.LoginResponseDTO
import br.com.enali.service.AuthClienteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/cliente")
class AuthClienteController(
    private val authService: AuthClienteService
) {

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun cadastrar(
        @Valid @RequestBody request: ClienteRequest
    ): ClienteResponse {
        return authService.cadastrar(request)
    }

    @PostMapping("/signin")
    fun login(
        @Valid @RequestBody request: LoginRequestDTO
    ): ResponseEntity<LoginResponseDTO> {
        return ResponseEntity.ok(authService.login(request))
    }
}