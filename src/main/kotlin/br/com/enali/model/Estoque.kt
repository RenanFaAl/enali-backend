
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

    // RN26: verificação de disponibilidade antes de confirmar o pedido
    fun possuiDisponibilidade(quantidade: Int): Boolean {
        return ativo && quantidade <= quantidadeDisponivel
    }

    // Política de reserva: a quantidade pedida sai do disponível e vai para o reservado
    fun reservar(quantidade: Int): Estoque {
        require(quantidade > 0) { "A quantidade deve ser maior que zero." }
        // RN54: nenhuma movimentação pode deixar o estoque negativo
        require(quantidade <= quantidadeDisponivel) {
            "Estoque insuficiente para o produto ${produto.nome}."
        }

        return copy(
            quantidadeDisponivel = quantidadeDisponivel - quantidade,
            quantidadeReservada = quantidadeReservada + quantidade
        )
    }

    // Cancelamento devolve a reserva ao disponível
    fun liberar(quantidade: Int): Estoque {
        require(quantidade > 0) { "A quantidade deve ser maior que zero." }
        // RN54: a reserva também não pode ficar negativa
        require(quantidade <= quantidadeReservada) {
            "Reserva insuficiente para o produto ${produto.nome}."
        }

        return copy(
            quantidadeDisponivel = quantidadeDisponivel + quantidade,
            quantidadeReservada = quantidadeReservada - quantidade
        )
    }
}
