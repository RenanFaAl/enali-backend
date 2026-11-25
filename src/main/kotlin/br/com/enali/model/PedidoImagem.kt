package br.com.enali.model

import jakarta.persistence.*

/** Foto anexada pelo cliente às observações do pedido (ex.: referência de personalização). */
@Entity
@Table(name = "pedido_imagens")
class PedidoImagem(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:Column(name = "pedido_id", nullable = false)
    val pedidoId: Long,

    @field:Column(name = "dados", columnDefinition = "BYTEA", nullable = false)
    val dados: ByteArray,

    @field:Column(name = "tipo_mime", length = 100, nullable = false)
    val tipoMime: String
)
