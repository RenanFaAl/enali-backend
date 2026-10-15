
package br.com.enali.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "estoques")
data class Estoque(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "produto_id", nullable = false)
    val produto: Produto,

    @field:Column(nullable = false)
    val quantidadeDisponivel: Int,

    @field:Column(nullable = false)
    val quantidadeReservada: Int = 0,

    @field:Column(nullable = false)
    val estoqueMinimo: Int = 0,

    @field:Column(nullable = false, length = 100)
    val localizacao: String,

    @field:Column(nullable = false)
    val ativo: Boolean = true,

    @field:Column(nullable = false)
    var dataAtualizacao: LocalDateTime = LocalDateTime.now()
) {

    @PrePersist
    @PreUpdate
    fun atualizarData() {
        dataAtualizacao = LocalDateTime.now()
    }
}
