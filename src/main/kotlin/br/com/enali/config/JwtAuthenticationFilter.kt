package br.com.enali.config

import br.com.enali.repository.AdministradorRepository
import br.com.enali.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val repository: AdministradorRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cabecalho = request.getHeader("Authorization")

        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            val token = cabecalho.substring(7)

            try {
                if (jwtService.validarToken(token)) {
                    val email = jwtService.extrairEmail(token)
                    val administrador = repository.findByEmail(email)

                    if (
                        administrador != null &&
                        administrador.ativo &&
                        SecurityContextHolder.getContext().authentication == null
                    ) {
                        val autoridade = SimpleGrantedAuthority(
                            "ROLE_${administrador.nivelAcesso}"
                        )

                        val autenticacao =
                            UsernamePasswordAuthenticationToken(
                                administrador.email,
                                null,
                                listOf(autoridade)
                            )

                        SecurityContextHolder.getContext()
                            .authentication = autenticacao
                    }
                }
            } catch (ex: Exception) {
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}
