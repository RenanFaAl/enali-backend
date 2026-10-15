
package br.com.enali.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "produtos")
data class Produto( 


    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, 

    @field:Column(nullable = false, length = 120)
    val nome: String,

    @field:Column(nullable = false, length = 500)
    val descricao: String,  


    @field:Column(nullable = false, precision = 12, scale = 2) 
    val preco: BigDecimal,

    @field:Column(nullable = false) 
    val disponivel: Boolean = true,

    @field:Column(nullable = false)
    val desconto: Double = 0.0
    
)
