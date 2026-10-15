package br.com.enali.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {

                // Autenticação pública
                it.requestMatchers(
                    HttpMethod.POST,
                    "/auth/cliente/signup",
                    "/auth/cliente/signin",
                    "/auth/admin/signin"
                ).permitAll()

                // Clientes: área pessoal
                it.requestMatchers(
                    "/clientes/me",
                    "/clientes/me/**"
                ).hasRole("CLIENTE")

                // Clientes: gerenciamento completo permitido aos administradores
                it.requestMatchers(
                    "/clientes",
                    "/clientes/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Administradores: área pessoal
                it.requestMatchers(
                    "/admin/me",
                    "/admin/me/**",
                    "/enderecos/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Administradores: gerenciamento exclusivo do ADMIN_MASTER
                it.requestMatchers(
                    "/admin",
                    "/admin/**"
                ).hasRole("ADMIN_MASTER")

                // Endereços: acesso para clientes e administradores
                it.requestMatchers(
                    "/enderecos",
                    "/enderecos/**"
                ).hasAnyRole("CLIENTE", "ADMIN_MASTER", "ADMIN_COMUM")

                // Pedidos: criação exclusiva de clientes
                it.requestMatchers(
                    HttpMethod.POST,
                    "/pedidos"
                ).hasRole("CLIENTE")

                // Pedidos: busca e listagem para administradores
                it.requestMatchers(
                    HttpMethod.GET,
                    "/pedidos/buscar",
                    "/pedidos"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Pedidos: consulta por ID
                it.requestMatchers(
                    HttpMethod.GET,
                    "/pedidos/*"
                ).hasAnyRole("CLIENTE", "ADMIN_MASTER", "ADMIN_COMUM")

                // Pedidos: cancelamento por cliente ou administrador
                it.requestMatchers(
                    HttpMethod.PATCH,
                    "/pedidos/*/cancelar"
                ).hasAnyRole("CLIENTE", "ADMIN_MASTER", "ADMIN_COMUM")

                // Materiais: acesso total (visualizar, criar, atualizar, alterar disponibilidade e excluir) restrito apenas aos administradores
                it.requestMatchers(
                    "/materiais",
                    "/materiais/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Fornecedores: acesso total (visualizar, criar, atualizar, alterar status e excluir) restrito apenas aos administradores
                it.requestMatchers(
                    "/fornecedores",
                    "/fornecedores/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Produtos: visualização (listagem, por ID e busca) permitida para clientes e administradores
                it.requestMatchers(
                    HttpMethod.GET,
                    "/produtos",
                    "/produtos/**",
                    "/produtos/buscar"
                ).hasAnyRole("CLIENTE", "ADMIN_MASTER", "ADMIN_COMUM")

                // Produtos: criação exclusivas para administradores
                it.requestMatchers(
                    HttpMethod.POST,
                    "/produtos"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Produtos: atualização exclusivas para administradores
                it.requestMatchers(
                    HttpMethod.PUT,
                    "/produtos/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Produtos: exclusão exclusivas para administradores
                it.requestMatchers(
                    HttpMethod.DELETE,
                    "/produtos/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Estoque: acesso total (visualizar, criar, alterar e excluir) restrito apenas aos administradores
                it.requestMatchers(
                    "/estoques",
                    "/estoques/**"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                // Qualquer outra rota exige autenticação
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}