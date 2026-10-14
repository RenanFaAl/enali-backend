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

                it.requestMatchers(
                    HttpMethod.POST,
                    "/auth/cliente/signup",
                    "/auth/cliente/signin",
                    "/auth/admin/signin"
                ).permitAll()

                it.requestMatchers(
                    HttpMethod.PATCH,
                    "/clientes/*/ativo"
                ).hasAnyRole("ADMIN_MASTER", "ADMIN_COMUM")

                it.requestMatchers(
                    "/clientes/me",
                    "/clientes/me/**"
                ).hasRole("CLIENTE")

                it.requestMatchers(
                    "/clientes",
                    "/clientes/**"
                ).hasRole("ADMIN_MASTER")


                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
