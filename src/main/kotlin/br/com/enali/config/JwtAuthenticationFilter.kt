package br.com.enali.config

import br.com.enali.repository.AdministradorRepository
import br.com.enali.repository.ClienteRepository
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
    private val administradorRepository: AdministradorRepository,
    private val clienteRepository: ClienteRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cabecalho = request.getHeader("Authorization")

        if (
            cabecalho != null &&
            cabecalho.startsWith("Bearer ") &&
            SecurityContextHolder.getContext().authentication == null
        ) {
            val token = cabecalho.substring(7)

            try {
                if (jwtService.validarToken(token)) {
                    val email = jwtService.extrairEmail(token)
                    val tipoUsuario = jwtService.extrairTipoUsuario(token)

                    val usuario = when (tipoUsuario) {
                        "ADMINISTRADOR" -> {
                            val administrador =
                                administradorRepository.findByEmail(email)

                            if (administrador != null && administrador.ativo) {
                                UsuarioAutenticado(
                                    id = administrador.id,
                                    email = administrador.email,
                                    tipoUsuario = "ADMINISTRADOR",
                                    nivelAcesso = administrador.nivelAcesso
                                )
                            } else {
                                null
                            }
                        }

                        "CLIENTE" -> {
                            val cliente = clienteRepository.findByEmail(email)

                            if (cliente != null && cliente.ativo) {
                                UsuarioAutenticado(
                                    id = cliente.id,
                                    email = cliente.email,
                                    tipoUsuario = "CLIENTE"
                                )
                            } else {
                                null
                            }
                        }

                        else -> null
                    }

                    if (usuario != null) {
                        val nomeAutoridade = when (usuario.tipoUsuario) {
                            "ADMINISTRADOR" ->
                                "ROLE_${usuario.nivelAcesso}"

                            "CLIENTE" -> "ROLE_CLIENTE"

                            else -> null
                        }

                        if (nomeAutoridade != null) {
                            val autenticacao =
                                UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    listOf(SimpleGrantedAuthority(nomeAutoridade))
                                )

                            SecurityContextHolder.getContext()
                                .authentication = autenticacao
                        }
                    }
                }
            } catch (ex: Exception) {
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}
