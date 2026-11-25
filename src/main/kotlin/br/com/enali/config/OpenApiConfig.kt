package br.com.enali.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Swagger em /swagger-ui.html, com o botão Authorize aceitando o token JWT do login. */
@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("API Enali")
                .description("Backend do Enali: produtos, estoque, clientes, endereços, pedidos, materiais e fornecedores.")
                .version("1.0")
        )
        .components(
            Components().addSecuritySchemes(
                "bearerAuth",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            )
        )
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
}
