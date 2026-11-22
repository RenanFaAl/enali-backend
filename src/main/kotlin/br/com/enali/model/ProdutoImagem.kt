package br.com.enali.model

import jakarta.persistence.*

@Entity
@Table(name = "produto_imagens")
data class ProdutoImagem(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "produto_id", nullable = false)
    val produto: Produto,

    @field:Column(name = "dados", columnDefinition = "BYTEA", nullable = false)
    val dados: ByteArray,

    @field:Column(name = "tipo_mime", length = 100, nullable = false)
    val tipoMime: String,

    @field:Column(nullable = false)
    val ordem: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ProdutoImagem
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}